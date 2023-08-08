package it.units.sim.yourtube;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.units.sim.yourtube.model.Category;
import it.units.sim.yourtube.model.UserSubscription;

public class CategoryNewFragment extends AbstractCategoryEditorFragment {

    private CategoriesViewModel categoriesViewModel;

    public CategoryNewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoriesViewModel = new ViewModelProvider(requireActivity()).get(CategoriesViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_edit, container, false);
        Button actionButton = view.findViewById(R.id.category_editor_action_button);
        actionButton.setText(R.string.create);
        return view;
    }

    @Override
    protected boolean createOrModifyCategory() {
        if (categoryName.length() == 0) {
            failureReason = "You need to specify a name for the category";
            return false;
        }
        if (chosenCategoryResId == 0) {
            failureReason = "You need to pick an icon";
            return false;
        }

        for (Category c : Objects.requireNonNull(categoriesViewModel.getCategoriesList().getValue())) {
            if (c.getName().equals(categoryName)) {
                failureReason = "This category already exists";
                return false;
            }
        }

        List<String> selectedChannelsId = selectedChannels
                .stream()
                .map(UserSubscription::getChannelId)
                .collect(Collectors.toList());
        Category newCategory = new Category(categoryName, selectedChannelsId, chosenCategoryResId);
        categoriesViewModel.addCategory(newCategory);
        return true;
    }

    @Override
    protected void showSuccessSnackbarMessage(View parentView) {
        Snackbar.make(parentView, "Category " + categoryName + " created!", Snackbar.LENGTH_SHORT).show();
    }
}