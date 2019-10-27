package me.xdark.launcher;

import java.nio.file.Path;
import java.util.List;

public interface Tweaker {

    void prepare(Path gameDirectory, Path assetsDirectory, String version);

    void injectArguments(List<String> arguments);

    void injectIntoLauncher(Launcher launcher);
}
