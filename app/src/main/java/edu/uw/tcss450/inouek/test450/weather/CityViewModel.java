package edu.uw.tcss450.inouek.test450.weather;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class CityViewModel extends ViewModel {
    /**
     * This ViewModel will be a singleton, meaning there will be only one instantiation and this
     * static variable will reference it. To implement this design pattern, we need a factory
     * method. See getFactory()
     */
    private static CityViewModel mInstance;

    /**
     * Stores the Location object wrapped in a MutableLiveData. MutableLiveData implements a flavor
     * of the Observer Design Pattern. We will add Observers to this state in the fragments.
     */
    private MutableLiveData<ArrayList<CityPost>> cities;


    /**
     * Private to limit instantiation to this class and this class only. To obtain one (the only
     * one) of these objects, users must call the factory method.
     */
    private CityViewModel() {
        cities = new MutableLiveData<>();
    }


    /**
     * Obtain access to the LiveData object. This is usually done to allow the addition and removal
     * of Observers. Note the Polymorphic return type.
     * @return the LaveData to observe
     */
    public LiveData<ArrayList<CityPost>> getCityList() {
        return cities;
    }


    /**
     * Change the Location state of this ViewModel.
     * @param "List<TenDaysWeatherPost>" the new Location
     */
    public void changeData(final ArrayList<CityPost> theCities) {
        this.cities.getValue().addAll(theCities);
    }


    /**
     * Factory method. This ViewModel is a singleton. Use this factory method to obtain a
     * ViewModelProvider.Factory that can then be used to obtain the ViewModel instance.
     * @return a factory to provide instances of this ViewModel
     */
    public static ViewModelProvider.Factory getFactory() {
        return new ViewModelProvider.Factory() {

            @NonNull
            @Override
            public CityViewModel create(@NonNull Class modelClass) {
                if (mInstance == null) {
                    mInstance = new CityViewModel();
                }
                return mInstance;
            }
        };
    }

}
