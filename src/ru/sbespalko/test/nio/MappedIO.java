/**
 *
 */
package ru.sbespalko.test.nio;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author sergey
 *
 */
public class MappedIO {
    private int  numOfInts      = 1000_000;
    private int  numOfUbuffInts = numOfInts * Character.BYTES;
    private Path streamFile     = Paths.get("stream.tmp");
    private Path mappedFile     = Paths.get("mapped.tmp");

    private abstract class Tester {
        private String name;

        public Tester(String name) {
            this.name = name;
        }

        public void runTest() throws IOException {
            System.out.println(name + ": ");
            try {
                long start = System.nanoTime();
                test();
                long duration = System.nanoTime() - start;
                System.out.format("%2f%n", duration / 1.0e9);
            } catch (IOException e) {
                throw new IOException(e);
            }
        }

        public abstract void test() throws IOException;
    }

    private Tester[] tests = { new Tester("Stream write") {
        @Override
        public void test() throws IOException {
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(streamFile.toFile())))) {
                for (Integer i = 0; i < numOfInts; i++) {
                    out.write(Integer.toString(i % 10));
                }
            }

        }
    }, new Tester("Mapped write") {
        @Override
        public void test() throws IOException {
            try (BufferedWriter out = Files.newBufferedWriter(mappedFile, WRITE, CREATE)) {
                // StringBuilder str = new StringBuilder("");
                for (int i = 0; i < numOfInts; i++) {
                    // str.append(i).append(' ');
                    out.write(Integer.toString(i % 10));
                }
                // out.write(str.toString());
            }
        }
    }, new Tester("Stream read") {
        @Override
        public void test() throws IOException {
            try (BufferedReader in = new BufferedReader(new FileReader(streamFile.toFile()))) {
                StringBuilder text = new StringBuilder("");
                text.append(in.readLine());
                text.append(" ");
                // for (int i = 0; i < numOfInts; i++) {
                // text.append(in.read());
                // }
                // System.out.println(text);
            }
        }
    }, new Tester("Mapped read") {
        @Override
        public void test() throws IOException {
            try (BufferedReader in = Files.newBufferedReader(mappedFile)) {
                StringBuilder text = new StringBuilder("");
                text.append(in.readLine());
                text.append(" ");
                // for (int i = 0; i < numOfInts; i++) {
                // text.append(in.read());
                // }
                // System.out.println(text);
            }
        }
    }, new Tester("Stream Read/Write") {
        @Override
        public void test() throws IOException {
            /*
             * try (RandomAccessFile raf = new RandomAccessFile(streamFile.toFile(), "rw")) { raf.writeInt(1); for (int i = 0; i < numOfInts; i++) {
             * raf.seek(raf.length() - 4); raf.writeInt(raf.readInt()); } System.out.println(Files.size(streamFile)); }
             */
        }
    }, new Tester("Mapped Read/Write") {
        @Override
        public void test() throws IOException {
            try (FileChannel fchan = FileChannel.open(mappedFile, READ, WRITE)) {
                ByteBuffer out = ByteBuffer.allocate(numOfUbuffInts);
                System.out.println("capacity=" + out.capacity() + " limit=" + out.limit());
                ByteBuffer add = ByteBuffer.wrap("Bespalko".getBytes());
                while ((fchan.read(out) != -1) && out.hasRemaining()) {
                }
                fchan.position(0);
                while (add.hasRemaining()) {
                    fchan.write(add);
                }
                out.flip();
                System.out.println("flip\ncapacity=" + out.capacity() + " limit=" + out.limit());
                while (out.hasRemaining()) {
                    fchan.write(out);
                }

            }
        }
    }

    };

    public static void main(String[] args) throws IOException {
        MappedIO mappedIO = new MappedIO();
        Files.deleteIfExists(mappedIO.mappedFile);
        Files.deleteIfExists(mappedIO.streamFile);
        for (Tester test : mappedIO.tests) {
            test.runTest();
        }
    }
}
