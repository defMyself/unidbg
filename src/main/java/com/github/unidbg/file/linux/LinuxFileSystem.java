package com.github.unidbg.file.linux;

import com.github.unidbg.Emulator;
import com.github.unidbg.file.BaseFileSystem;
import com.github.unidbg.file.FileSystem;
import com.github.unidbg.file.FileResult;
import com.github.unidbg.file.Stdin;
import com.github.unidbg.linux.file.*;
import com.github.unidbg.unix.IO;

import java.io.File;

public class LinuxFileSystem extends BaseFileSystem<AndroidFileIO> implements FileSystem<AndroidFileIO>, IOConstants {

    public LinuxFileSystem(Emulator<AndroidFileIO> emulator, File rootDir) {
        super(emulator, rootDir);
    }

    @Override
    public FileResult<AndroidFileIO> open(String pathname, int oflags) {
        if ("/dev/tty".equals(pathname)) {
            return FileResult.<AndroidFileIO>success(new NullFileIO(pathname));
        }
        if ("/proc/self/maps".equals(pathname) || ("/proc/" + emulator.getPid() + "/maps").equals(pathname)) {
            return FileResult.<AndroidFileIO>success(new MapsFileIO(oflags, pathname, emulator.getMemory().getLoadedModules()));
        }

        return super.open(pathname, oflags);
    }

    @Override
    public FileResult<AndroidFileIO> createSimpleFileIO(File file, int oflags, String path) {
        return FileResult.<AndroidFileIO>success(new SimpleFileIO(oflags, file, path));
    }

    @Override
    public FileResult<AndroidFileIO> createDirectoryFileIO(File file, int oflags, String path) {
        return FileResult.<AndroidFileIO>success(new DirectoryFileIO(oflags, path, file));
    }

    @Override
    protected AndroidFileIO createStdin(int oflags) {
        return new Stdin(oflags);
    }

    @Override
    protected AndroidFileIO createStdout(int oflags, File stdio, String pathname) {
        return new Stdout(oflags, stdio, pathname, IO.STDERR.equals(pathname), null);
    }

    @Override
    protected boolean hasCreat(int oflags) {
        return (oflags & O_CREAT) != 0;
    }

    @Override
    protected boolean hasDirectory(int oflags) {
        return (oflags & O_DIRECTORY) != 0;
    }

    @Override
    protected boolean hasAppend(int oflags) {
        return (oflags & O_APPEND) != 0;
    }

}