const WebSocket = require('ws');

async function testRouting() {
    const serverUrl = 'ws://localhost:3000';
    
    // Connect User A (Judge 1)
    const wsA = new WebSocket(`${serverUrl}?userId=JudgeA`);
    
    wsA.on('open', () => {
        console.log('Judge A connected');
    });

    wsA.on('message', (data) => {
        const msg = JSON.parse(data);
        console.log('Judge A received:', msg);
    });

    // Connect User B (Judge 2)
    const wsB = new WebSocket(`${serverUrl}?userId=JudgeB`);
    
    wsB.on('open', () => {
        console.log('Judge B connected');
        
        // Send message from B to A
        const packet = JSON.stringify({
            type: 'send_message',
            senderId: 'JudgeB',
            receiverId: 'JudgeA',
            encryptedPayload: 'ENCRYPTED_DATA_FROM_B_TO_A'
        });
        wsB.send(packet);
        console.log('Judge B sent message to Judge A');
    });

    wsB.on('message', (data) => {
        const msg = JSON.parse(data);
        console.log('Judge B received:', msg);
    });

    // Wait a bit and then close
    setTimeout(() => {
        wsA.close();
        wsB.close();
        process.exit(0);
    }, 2000);
}

testRouting().catch(console.error);
