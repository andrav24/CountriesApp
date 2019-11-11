package tech.andrav.countriesapp.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import tech.andrav.countriesapp.model.CountriesService;
import tech.andrav.countriesapp.model.CountryModel;

public class ListViewModel extends ViewModel {

    public MutableLiveData<List<CountryModel>> countries = new MutableLiveData<>();
    public MutableLiveData<Boolean> countryLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();

    private CountriesService countriesService = CountriesService.getInstance();

    //RxJava
    private CompositeDisposable disposable = new CompositeDisposable();

    /**
     * This method is entry point for the View in our ViewModel
     */
    public void refresh() {

        fetchCountries();
    }

    private void fetchCountries() {
        loading.setValue(true);
        disposable.add(
                countriesService.getCountries()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<CountryModel>>() {

                    @Override
                    public void onSuccess(List<CountryModel> countryModels) {
                        countries.setValue(countryModels);
                        countryLoadError.setValue(false);
                        loading.setValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        countryLoadError.setValue(true);
                        loading.setValue(false);
                        e.printStackTrace();
                    }
                })
        );

    }

    /**
     * Если система killed the app этот метод убьет поток в котором было запущено ...
     * то, что была в disposable и у нас не будет утечек памяти.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
