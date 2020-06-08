package com.github

import org.eclipse.jgit.diff.RawText
import org.eclipse.jgit.diff.RawTextComparator
import kotlin.experimental.and

class AutoCRLFComparator : RawTextComparator() {
    override fun equals(a: RawText, ai: Int, b: RawText, bi: Int): Boolean {
        var line1 = a.getString(ai)
        var line2 = b.getString(bi)
        line1 = trimTrailingEoL(line1)
        line2 = trimTrailingEoL(line2)
        return line1 == line2
    }

    override fun hashRegion(raw: ByteArray, ptr: Int, end: Int): Int {
        var ptr = ptr
        var end = end
        var hash = 5381
        end = trimTrailingEoL(raw, ptr, end)
        while (ptr < end) {
            hash += (hash shl 5) + (raw[ptr] and 0xff.toByte())
            ptr++
        }
        return hash
    }

    companion object {
        private fun trimTrailingEoL(line: String): String {
            var end = line.length - 1
            while (end >= 0 && isNewLine(line[end])) {
                --end
            }
            return line.substring(0, end + 1)
        }

        private fun trimTrailingEoL(raw: ByteArray, start: Int, end: Int): Int {
            var ptr = end - 1
            while (start <= ptr && (raw[ptr].toChar() == '\r' || raw[ptr].toChar() == '\n')) {
                ptr--
            }
            return ptr + 1
        }

        private fun isNewLine(ch: Char): Boolean {
            return ch == '\n' || ch == '\r'
        }
    }
}
