package tech.andrav.countriesapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.andrav.countriesapp.R;
import tech.andrav.countriesapp.model.CountryModel;
import tech.andrav.countriesapp.viewmodel.ListViewModel;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.countriesList)
    RecyclerView countriesList;

    @BindView(R.id.list_error)
    TextView listError;

    @BindView(R.id.loading_view)
    ProgressBar loadingView;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    private ListViewModel viewModel;
    private CountryListAdapter adapter = new CountryListAdapter(new ArrayList<>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        /**
         * The reason to do this is because lifecycle. The activity is very transient.
         * ListViewModel have a larger lifecycle than activity.
         * When activity is destroyed and recreate Provider give the activity the exactly the same viewModel...
         * and we never lose any data.
         */
        viewModel = ViewModelProviders.of(this).get(ListViewModel.class);
        viewModel.refresh();

        countriesList.setLayoutManager(new LinearLayoutManager(this));
        countriesList.setAdapter(adapter);

        observerViewModel();
    }

    /**
     * Here is were we actually attach this view with LiveData variables and retreave some information from there.
     */
    private void observerViewModel() {
        viewModel.countries.observe(this, new Observer<List<CountryModel>>() {
            @Override
            public void onChanged(List<CountryModel> countryModels) {
                if (countryModels != null) {
                    countriesList.setVisibility(View.VISIBLE);
                    adapter.updateCountries(countryModels);
                }
            }
        });

        viewModel.countryLoadError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isError) {
                if (isError != null) {
                    listError.setVisibility(isError ? View.VISIBLE : View.GONE);
                }
            }
        });

        viewModel.loading.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if (isLoading != null) {
                    loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                    if (isLoading) {
                        listError.setVisibility(View.GONE);
                        countriesList.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
}
