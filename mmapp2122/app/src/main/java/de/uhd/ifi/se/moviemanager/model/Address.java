package de.uhd.ifi.se.moviemanager.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Locale;
import java.util.Objects;

/**
 * Models an address with postal code, city, street name, and house number.
 *
 * The Builder design pattern is used to create an object, see {@link #builder()}.
 */
public class Address implements Parcelable {
    public static final int NO_POSTAL_CODE = -1;
    public static final int NO_HOUSE_NUMBER = -1;
    public static final Address DEFAULT_ADDRESS = Address.builder().build();

    private int postalCode;
    private String city;
    private String streetName;
    private int houseNumber;

    @JsonCreator
    private Address(@JsonProperty("postalCode") int postalCode,
                    @JsonProperty("city") String city,
                    @JsonProperty("streetName") String streetName,
                    @JsonProperty("houseNumber") int houseNumber) {
        this.postalCode = postalCode;
        this.city = city;
        this.streetName = streetName;
        this.houseNumber = houseNumber;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getStreetName() {
        return streetName;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.US,
                "%06d %s, %s Nr. %03d",
                postalCode, city, streetName, houseNumber
        );
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (!(object instanceof Address)) {
            return false;
        }
        Address address = (Address) object;
        return address.getPostalCode() == postalCode
                && address.getCity().equals(city)
                && address.getStreetName().equals(streetName)
                && address.getHouseNumber() == houseNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(postalCode, city, streetName, houseNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Method to build the {@link Address}, uses the Builder design pattern. The Builder design
     * pattern allows us to write readable, understandable code to set up complex objects.
     *
     * @return Builder object that can be further extended, e.g. with methods {@link
     * AddressBuilder#setPostalCode(int)} or {@link AddressBuilder#setStreetName(String)}. To create the {@link
     * Address} object, use {@link AddressBuilder#build()}.
     */
    public static AddressBuilder builder() {
        return new AddressBuilder();
    }

    /**
     * Necessary to pass objects from one Android activity to another activity via {@link
     * android.content.Intent}s as {@link Parcel}s.
     */
    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel source) {
            return new Address(source);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    /**
     * Necessary to pass objects from one Android activity to another activity via {@link
     * android.content.Intent}s as {@link Parcel}s.
     */
    private Address(Parcel parcel) {
        postalCode = parcel.readInt();
        city = parcel.readString();
        streetName = parcel.readString();
        houseNumber = parcel.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(postalCode);
        dest.writeString(city);
        dest.writeString(streetName);
        dest.writeInt(houseNumber);
    }

    /**
     * Builder class to create an {@link Address}, uses the Builder design pattern. The Builder
     * design pattern allows us to write readable, understandable code to set up complex objects.
     */
    public static class AddressBuilder {
        private int postalCode;
        private String city;
        private String streetName;
        private int houseNumber;

        private AddressBuilder() {
            postalCode = NO_POSTAL_CODE;
            city = "";
            streetName = "";
            houseNumber = NO_HOUSE_NUMBER;
        }

        public AddressBuilder setPostalCode(int postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public AddressBuilder setCity(String city) {
            this.city = city;
            return this;
        }

        public AddressBuilder setStreetName(String streetName) {
            this.streetName = streetName;
            return this;
        }

        public AddressBuilder setHouseNumber(int houseNumber) {
            this.houseNumber = houseNumber;
            return this;
        }

        public Address build() {
            return new Address(postalCode, city, streetName, houseNumber);
        }
    }
}
