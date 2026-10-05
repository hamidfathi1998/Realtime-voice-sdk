package ir.hfathi.sdk.protocol

import java.util.zip.CRC32

fun calculateChecksum(payload: ByteArray): UInt =
    CRC32().apply { update(payload) }.value.toUInt()