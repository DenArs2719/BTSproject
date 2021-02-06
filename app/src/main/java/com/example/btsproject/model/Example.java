package com.example.btsproject.model;
import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Example implements Parcelable
{
//аааааааааа
    @SerializedName("cast")
    @Expose
    private List<Cast> cast = new ArrayList<>();
    @SerializedName("crew")
    @Expose
    private List<Result> crew = new ArrayList<>();
    @SerializedName("id")
    @Expose
    private Integer id;
    public final static Parcelable.Creator<Example> CREATOR = new Creator<Example>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Example createFromParcel(Parcel in) {
            return new Example(in);
        }

        public Example[] newArray(int size) {
            return (new Example[size]);
        }

    }
            ;

    protected Example(Parcel in) {
        in.readList(this.cast, (com.example.btsproject.model.Cast.class.getClassLoader()));
        in.readList(this.crew, (com.example.btsproject.model.Crew.class.getClassLoader()));
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public Example() {
    }

    public List<Cast> getCast() {
        return cast;
    }

    public void setCast(List<Cast> cast) {
        this.cast = cast;
    }

    public List<Result> getCrew() {
        return crew;
    }

    public void setCrew(List<Result> crew) {
        this.crew = crew;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(cast);
        dest.writeList(crew);
        dest.writeValue(id);
    }

    public int describeContents() {
        return 0;
    }

}