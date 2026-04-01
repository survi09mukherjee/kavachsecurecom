/**
 * Utility for AES-GCM End-to-End Encryption compatible with Android's CryptoUtils.kt.
 * 
 * Replaces the mock base64 legacy implementation to enable true cross-platform
 * messaging between Android and Web clients.
 */
export class CryptoUtils {
    // 32-byte key generated from "KavachSecureCommDemoKey1234567890" (exactly matches Android app's DEMO_KEY)
    private static DEMO_KEY_STRING = "KavachSecureCommDemoKey1234567890".substring(0, 32);
    private static keyCache: CryptoKey | null = null;
    
    private static async getAESKey(): Promise<CryptoKey> {
        if (this.keyCache) return this.keyCache;
        const keyData = new TextEncoder().encode(this.DEMO_KEY_STRING);
        this.keyCache = await window.crypto.subtle.importKey(
            "raw", 
            keyData, 
            { name: "AES-GCM", length: 256 }, 
            false, 
            ["encrypt", "decrypt"]
        );
        return this.keyCache;
    }

    private static arrayBufferToBase64(buffer: ArrayBuffer): string {
        let binary = '';
        const bytes = new Uint8Array(buffer);
        for (let i = 0; i < bytes.byteLength; i++) {
            binary += String.fromCharCode(bytes[i]);
        }
        return btoa(binary);
    }

    private static base64ToArrayBuffer(base64: string): ArrayBuffer {
        const binary = atob(base64);
        const bytes = new Uint8Array(binary.length);
        for (let i = 0; i < binary.length; i++) {
            bytes[i] = binary.charCodeAt(i);
        }
        return bytes.buffer;
    }

    static async encrypt(plaintext: string): Promise<string> {
        const key = await this.getAESKey();
        const iv = window.crypto.getRandomValues(new Uint8Array(12));
        const encodedText = new TextEncoder().encode(plaintext);
        
        const cipherBuffer = await window.crypto.subtle.encrypt(
            { name: "AES-GCM", iv: iv },
            key,
            encodedText
        );
        
        // Android's implementation concatenates IV and CipherText
        const combined = new Uint8Array(12 + cipherBuffer.byteLength);
        combined.set(iv, 0);
        combined.set(new Uint8Array(cipherBuffer), 12);
        
        return this.arrayBufferToBase64(combined.buffer);
    }

    static async decrypt(encodedCipherText: string): Promise<string> {
        try {
             // Let's also support fallback for older mock packets if necessary, but we want strict parity.
             if (encodedCipherText.startsWith("KAVACH_")) return encodedCipherText;

             const combinedBuffer = this.base64ToArrayBuffer(encodedCipherText);
             const iv = combinedBuffer.slice(0, 12);
             const cipherText = combinedBuffer.slice(12);
             
             const key = await this.getAESKey();
             
             const decryptedBuffer = await window.crypto.subtle.decrypt(
                 { name: "AES-GCM", iv: new Uint8Array(iv) },
                 key,
                 cipherText
             );
             
             return new TextDecoder().decode(decryptedBuffer);
        } catch (e) {
             console.error("Decryption failed:", e);
             return "Error: Decryption Failed";
        }
    }
}
