package net.jpountz.lz4;

import java.nio.ByteBuffer;



/**
 * LZ4 decompressor that requires the size of the original input to be known.
 * Use {@link LZ4SafeDecompressor} if you only know the size of the
 * compressed stream.
 * <p>
 * Instances of this class are thread-safe.
 */
public abstract class LZ4FastDecompressor implements LZ4Decompressor {

    /**
     * Decompress <code>src[srcOff:]</code> into <code>dest[destOff:destOff+destLen]</code>
     * and return the number of bytes read from <code>src</code>.
     * <code>destLen</code> must be exactly the size of the decompressed data.
     *
     * @param destLen the <b>exact</b> size of the original input
     * @return the number of bytes read to restore the original input
     */
    public abstract int decompress(byte[] src, int srcOff, byte[] dest, int destOff, int destLen);

    /**
     * Decompress <code>src[srcOff:]</code> into <code>dest[destOff:destOff+destLen]</code>
     * and return the number of bytes read from <code>src</code>.
     * <code>destLen</code> must be exactly the size of the decompressed data.
     * The positions and limits of the {@link ByteBuffer}s remain unchanged.
     *
     * @param destLen the <b>exact</b> size of the original input
     * @return the number of bytes read to restore the original input
     */
    public abstract int decompress(ByteBuffer src, int srcOff, ByteBuffer dest, int destOff, int destLen);

    /**
     * Convenience method, equivalent to calling
     * {@link #decompress(byte[], int, byte[], int, int) decompress(src, 0, dest, 0, destLen)}.
     */
    public final int decompress(byte[] src, byte[] dest, int destLen) {
        return decompress(src, 0, dest, 0, destLen);
    }

    /**
     * Convenience method, equivalent to calling
     * {@link #decompress(byte[], byte[], int) decompress(src, dest, dest.length)}.
     */
    public final int decompress(byte[] src, byte[] dest) {
        return decompress(src, dest, dest.length);
    }

    /**
     * Convenience method which returns <code>src[srcOff:?]</code>
     * decompressed.
     * <p><b><span style="color:red">Warning</span></b>: this method has an
     * important overhead due to the fact that it needs to allocate a buffer to
     * decompress into.</p>
     * <p>Here is how this method is implemented:</p>
     * <pre>
     * final byte[] decompressed = new byte[destLen];
     * decompress(src, srcOff, decompressed, 0, destLen);
     * return decompressed;
     * </pre>
     */
    public final byte[] decompress(byte[] src, int srcOff, int destLen) {
        final byte[] decompressed = new byte[destLen];
        decompress(src, srcOff, decompressed, 0, destLen);
        return decompressed;
    }

    /**
     * Convenience method, equivalent to calling
     * {@link #decompress(byte[], int, int) decompress(src, 0, destLen)}.
     */
    public final byte[] decompress(byte[] src, int destLen) {
        return decompress(src, 0, destLen);
    }

    /**
     * Decompress <code>src</code> into <code>dest</code>. <code>dest</code>'s
     * {@link ByteBuffer#remaining()} must be exactly the size of the decompressed
     * data. This method moves the positions of the buffers.
     */
    public final void decompress(ByteBuffer src, ByteBuffer dest) {
        final int read = decompress(src, src.position(), dest, dest.position(), dest.remaining());
        dest.position(dest.limit());
        src.position(src.position() + read);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
