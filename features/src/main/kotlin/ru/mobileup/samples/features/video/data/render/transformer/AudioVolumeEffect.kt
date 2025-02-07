package ru.mobileup.samples.features.video.data.render.transformer

import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import java.nio.ByteBuffer

@UnstableApi
class AudioVolumeEffect(private var volume: Float = 1.0f) : AudioProcessor {
    private var inputAudioFormat: AudioProcessor.AudioFormat? = null
    private var outputAudioFormat: AudioProcessor.AudioFormat? = null
    private var buffer: ByteBuffer = ByteBuffer.allocate(0)
    private var isInputEnded = false

    override fun configure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        this.inputAudioFormat = inputAudioFormat
        this.outputAudioFormat = inputAudioFormat
        return inputAudioFormat
    }

    override fun isActive(): Boolean {
        // The processor is active if the volume is different from 1f
        return volume != 1.0f
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        if (buffer.capacity() < inputBuffer.remaining()) {
            buffer = ByteBuffer.allocate(inputBuffer.remaining())
        } else {
            buffer.clear()
        }

        while (inputBuffer.hasRemaining()) {
            val sample = inputBuffer.short / 32768f // Convert to range [-1.0, 1.0]
            val adjustedSample = (sample * volume).coerceIn(-1.0f, 1.0f) // Apply volume
            buffer.putShort((adjustedSample * 32768).toInt().toShort()) // Return to PCM
        }

        buffer.flip()
    }

    override fun queueEndOfStream() {
        isInputEnded = true
    }

    override fun getOutput(): ByteBuffer {
        return buffer
    }

    override fun isEnded(): Boolean {
        return isInputEnded && !buffer.hasRemaining()
    }

    override fun flush() {
        buffer.clear()
        isInputEnded = false
    }

    override fun reset() {
        inputAudioFormat = null
        outputAudioFormat = null
        buffer = ByteBuffer.allocate(0)
        isInputEnded = false
    }
}