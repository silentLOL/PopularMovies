package at.stefanirndorfer.popularmovies.database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class DataConverter {

    private static Gson gson = new Gson();

    @TypeConverter
    public static List<Integer> stringToIntegerList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Integer>>() {
        }
                .getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String integerListToString(List<Integer> integers) {
        return gson.toJson(integers);
    }

}
