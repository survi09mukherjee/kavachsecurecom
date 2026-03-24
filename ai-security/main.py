from fastapi import FastAPI, Request
from pydantic import BaseModel
import time

app = FastAPI(title="Kavach SecureComm - AI Security Module")

class MessagingMetadata(BaseModel):
    sender_id: str
    receiver_id: str
    payload_size_bytes: int
    timestamp: float

# Simple anomaly detection state (in-memory for demo, use Redis in prod)
user_message_rates = {}

@app.post("/api/v1/analyze-traffic")
async def analyze_traffic(metadata: MessagingMetadata):
    """
    Analyzes only the metadata of the encrypted traffic to detect anomalies.
    Does NOT have access to the unencrypted payload.
    """
    sender = metadata.sender_id
    current_time = time.time()
    
    if sender not in user_message_rates:
         user_message_rates[sender] = []
         
    # Keep only last 60 seconds
    user_message_rates[sender] = [t for t in user_message_rates[sender] if current_time - t < 60]
    user_message_rates[sender].append(current_time)
    
    # 1. High Frequency Anomaly (e.g., bot/DDoS inside the network)
    if len(user_message_rates[sender]) > 50:
        return {"status": "anomaly_detected", "reason": "high_frequency_messaging", "risk_score": 0.85}
        
    # 2. Huge Payload Anomaly (e.g., data exfiltration)
    if metadata.payload_size_bytes > 5_000_000: # 5MB limit on standard text/small image
        return {"status": "anomaly_detected", "reason": "unusually_large_payload", "risk_score": 0.70}
        
    return {"status": "normal", "risk_score": 0.0}

@app.get("/health")
async def health_check():
    return {"status": "healthy"}
