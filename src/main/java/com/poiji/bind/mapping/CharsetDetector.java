package com.poiji.bind.mapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static java.util.Comparator.comparing;

public final class CharsetDetector {

    private final Map<byte[], String> charsets;
    private String charset;

    public CharsetDetector() {
        charsets = new HashMap<>();
        charsets.put(new byte[]{-17, -69, -65}, "UTF-8");
        charsets.put(new byte[]{-2, -1}, "UTF-16BE");
        charsets.put(new byte[]{-1, -2}, "UTF-16LE");
        charsets.put(new byte[]{0, 0, -2, -1}, "UTF-32BE");
        charsets.put(new byte[]{-1, -2, 0, 0}, "UTF-32LE");
    }

    /**
     * Detects charset by BOM if it exists. Charset stored in property.
     * @param data first bytes of data. Should be 4 of more.
     * @return amount of BOM bytes
     */
    public int detect(final byte[] data){
        if (charset == null){
            for (int counter = 0; counter < 4; counter++) {
                final Iterator<Map.Entry<byte[], String>> iterator = charsets.entrySet().iterator();
                while (iterator.hasNext()){
                    final Map.Entry<byte[], String> entry = iterator.next();
                    final byte[] key = entry.getKey();
                    if (key.length > counter){
                        if (key[counter] != data[counter]){
                            iterator.remove();
                        }
                    }
                }
            }
            final Optional<Map.Entry<byte[], String>> maxEntry =
                charsets.entrySet().stream().max(comparing(entry -> entry.getKey().length));
            charset = maxEntry.map(Map.Entry::getValue).orElse(null);
            final Optional<Integer> maxKeyLength = maxEntry.map(entry -> entry.getKey().length);
            if (maxKeyLength.isPresent()){
                final int keyLength = maxKeyLength.get();
                System.arraycopy(data, keyLength, data, 0, data.length - keyLength);
                Arrays.fill(data, data.length - keyLength, data.length, (byte) 0);
                return keyLength;
            } else {
                return 0;
            }
        }
        return 0;
    }

    public Optional<String> getCharset() {
        return Optional.ofNullable(charset);
    }
}
