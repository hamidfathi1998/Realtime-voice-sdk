package ir.hfathi.sdk.protocol

class SequenceManager {
    private var nextSequence: UInt = 1u

    fun nextSequenceNumber(): UInt = nextSequence++

    fun validate(receivedSequence: UInt): Result<Unit> = TODO()
}