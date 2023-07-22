package com.chudiko;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;

public class HomeWorkExplaining {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Start program");
        InputStream inputStream = new Reader(100);
        OutputStream outputStream = new Writer();

        System.out.println(inputStream);
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new CopyFileInThread(inputStream, outputStream));
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        byte[] outputBytes = ((Writer)outputStream).getByteArrayOutputStream().toByteArray();
        assert(outputBytes == ((Reader)inputStream).getData());

        System.out.println(Arrays.toString(outputBytes));
        System.out.println("End of program");
    }
}

    class CopyFileInThread implements Runnable {
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public CopyFileInThread(InputStream inputStream, OutputStream outputStream) {
            this.inputStream = inputStream;
            this.outputStream = outputStream;
        }

        @Override
        public void run() {
            int count;
            byte[] bytes = new byte[10];
            try {
                while (true) {
                    synchronized (inputStream) {
                        count = inputStream.read(bytes);
                        if (count <= 0) break;
                        outputStream.write(Arrays.copyOf(bytes, count));
                    }
                    Thread.sleep(10);
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

     class Reader extends InputStream {
        private final byte[] data;
        public int index = 0;

         public byte[] getData() {
             return data;
         }

         Reader(int size) {
            data = new byte[size];
            init();
        }

        private void init() {
            new Random().nextBytes(data);
        }

        @Override
        public int read() throws IOException {
            if (index >= data.length) return -1;
            return data[index++];
        }

        @Override
        public int read(byte[] bytes) {
            int count = Math.min(data.length - index, bytes.length);
            System.arraycopy(data, index, bytes, 0, count);
            if (index + count <= data.length) {
                index += count;
                return count;
            }
            return 0;
        }

        @Override
        public String toString() {
            return Arrays.toString(data);
        }
    }

    class Writer extends OutputStream {
        private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        public ByteArrayOutputStream getByteArrayOutputStream() {
            return byteArrayOutputStream;
        }

        @Override
        public void write(int b) {
            byteArrayOutputStream.write(b);
        }

        @Override
        public void write(byte[] bytes) throws IOException {
            byteArrayOutputStream.write(bytes);
        }

        // Метод, который выводит содержимое буфера в консоль
        public void flushBuffer() {
            System.err.println(Thread.currentThread().getName() + " " + Arrays.toString(byteArrayOutputStream.toByteArray()));
            byteArrayOutputStream.reset();
        }
    }

