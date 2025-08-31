package it.s7danyy.taserutil;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

public class FriendManager {
    private static final Path FILE = FabricLoader.getInstance()
            .getConfigDir().resolve("taserutil_friends.json");
    private static final Gson GSON = new Gson();
    private static Set<String> friends = new LinkedHashSet<>();

    static {
        load();
    }

    public static Set<String> getFriends() {
        return new LinkedHashSet<>(friends);
    }

    public static boolean addFriend(String name) {
        if (friends.add(name)) { save(); return true; }
        return false;
    }

    public static boolean removeFriend(String name) {
        if (friends.remove(name)) { save(); return true; }
        return false;
    }

    private static void load() {
        File f = FILE.toFile();
        if (!f.exists()) return;
        try (Reader r = new FileReader(f)) {
            Type type = new TypeToken<Set<String>>() {}.getType();
            friends = GSON.fromJson(r, type);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static void save() {
        try (Writer w = new FileWriter(FILE.toFile())) {
            GSON.toJson(friends, w);
        } catch (IOException e) { e.printStackTrace(); }
    }
}
