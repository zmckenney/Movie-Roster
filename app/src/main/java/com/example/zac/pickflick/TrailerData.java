package com.example.zac.pickflick;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Zac on 11/7/15.
 */
public class TrailerData  implements Parcelable {

    private static final String LOG_TAG = MovieData.class.getSimpleName();


    String trailerName;
    final String TRAILER_BASE_URL = "https://www.youtube.com/watch?v=";
    String trailerURL;

    public TrailerData(String mKey, String mName) {

        this.trailerName = mName;
        this.trailerURL = TRAILER_BASE_URL + mKey;
    }

    private TrailerData(Parcel in) {
        trailerName = in.readString();
        trailerURL = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(trailerName);
        parcel.writeString(trailerURL);

    }



    public final Parcelable.Creator<TrailerData> CREATOR = new Parcelable.Creator<TrailerData>() {

        @Override
        public TrailerData createFromParcel(Parcel parcel) {
            return new TrailerData(parcel);
        }

        @Override
        public TrailerData[] newArray(int i) {
            return new TrailerData[i];
        }
    };



}
