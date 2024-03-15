package com.poiji.bind.mapping;

import com.poiji.exception.PoijiException;
import com.poiji.option.PoijiOptions;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

public final class XSSFStreamIterator<T> extends XSSFPoijiHandler<T> implements Iterator<T>{

    private final Semaphore canTake = new Semaphore(0);
    private final BlockingQueue<T> queue = new ArrayBlockingQueue<>(1000, true);

    XSSFStreamIterator(
        final PoijiOptions options,
        final ReadMappedFields mappedFields
    ) {
        super(options, mappedFields);
        this.consumer = this::put;
    }

    private void put(T t){
        try {
            queue.put(t);
            canTake.release();
        } catch (InterruptedException e) {
            throw new PoijiException(e.getMessage(), e);
        }
    }

    @Override
    public void endSheet() {
        canTake.release();
    }

    @Override
    public boolean hasNext() {
        try {
            canTake.acquire();
            return !queue.isEmpty();
        } catch (InterruptedException e) {
            throw new PoijiException(e.getMessage(), e);
        }
    }

    @Override
    public T next() {
        return queue.remove();
    }
}
