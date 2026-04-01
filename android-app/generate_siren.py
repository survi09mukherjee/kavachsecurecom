import math
import wave
import struct
sampleRate = 44100.0
duration = 4.0
frequency_low = 400.0
frequency_high = 800.0
obj = wave.open('c:/Users/DELL/Desktop/kavachsecurecom/android-app/app/src/main/res/raw/siren.wav','w')
obj.setnchannels(1)
obj.setsampwidth(2)
obj.setframerate(sampleRate)
phase = 0.0
for i in range(int(duration * sampleRate)):
    t = float(i) / sampleRate
    cycle_time = t % 2.0
    if cycle_time < 1.0:
        freq = frequency_low + (frequency_high - frequency_low) * cycle_time
    else:
        freq = frequency_high - (frequency_high - frequency_low) * (cycle_time - 1.0)
    phase += 2.0 * math.pi * freq / sampleRate
    value = int(32767.0 * math.sin(phase))
    data = struct.pack('<h', value)
    obj.writeframesraw(data)
obj.close()
