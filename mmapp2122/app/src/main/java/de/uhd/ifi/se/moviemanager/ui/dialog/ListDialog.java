package de.uhd.ifi.se.moviemanager.ui.dialog;

import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static de.uhd.ifi.se.moviemanager.ui.adapter.selection.SelectionAdapters.multiSelection;
import static de.uhd.ifi.se.moviemanager.ui.adapter.selection.SelectionAdapters.singleSelection;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.ImagePyramid;
import de.uhd.ifi.se.moviemanager.model.ModelObjectWithImage;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.ui.adapter.base.ContentBinder;
import de.uhd.ifi.se.moviemanager.ui.adapter.base.SimpleAdapter;
import de.uhd.ifi.se.moviemanager.ui.adapter.selection.MultiSelectionListener;
import de.uhd.ifi.se.moviemanager.ui.adapter.selection.SelectionAdapter;

/**
 * Dialog for a list of {@link Movie} or {@link Performer} objects. For example,
 * it is used to link movies and performers.
 *
 * The Builder design pattern is used to create the dialog, see {@link
 * #builder(FragmentActivity)}.
 */
public class ListDialog<T extends ModelObjectWithImage> extends DialogFragment {

    private Integer selectionLimit;

    private ConstraintLayout root;
    private TextView showTitle;
    private TextView showMessage;
    private SearchView search;
    private RecyclerView recyclerView;
    private Button positiveButton;
    private Button negativeButton;

    private Mode mode;
    private String message;
    private String title;

    private int listItemLayoutId =
            R.layout.listitem_model_object_with_image_detail;
    private ContentBinder<T> contentBinder;

    // all objects/elements in the list
    private List<T> objects;
    private List<T> selectedObjects;
    private BiPredicate<T, CharSequence> filterCriterion;
    private Adapter<? extends ViewHolder> adapter;
    // Text of the confirm/ok button
    private String positiveText;
    private Consumer<List<T>> positiveListener;
    // Text of the cancel button
    private String negativeText;
    private Runnable negativeListener;

    private ListDialog() {
        contentBinder = createBinder();
    }

    /**
     * Method to build the {@link ListDialog}, uses the Builder design pattern.
     * The Builder design pattern allows us to write readable, understandable
     * code to set up complex objects.
     *
     * @param context Android activity that the list dialog should be shown in.
     * @param <T>     {@link Movie}, {@link Performer}, {@link Tag} or {@link
     *                Renter} class.
     * @return Builder object that can be further extended, e.g. with methods
     * {@link ListDialogBuilder#setMode} or {@link ListDialogBuilder#setItems}.
     * To show the dialog, use {@link ListDialogBuilder#show()}.
     */
    public static <T extends ModelObjectWithImage> ListDialogBuilder<T> builder(
            FragmentActivity context) {
        return new ListDialogBuilder<>(context);
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getActivity() == null) {
            throw new IllegalStateException("Activity must be non-null");
        }

        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                if (negativeButton.getVisibility() == View.VISIBLE) {
                    negativeListener.run();
                }
                dismiss();
            }
        };
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_portrayable_list, container);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        selectedObjects = new ArrayList<>();

        root = view.findViewById(R.id.root);
        showTitle = view.findViewById(R.id.dialog_title);
        showMessage = view.findViewById(R.id.dialog_message);
        search = view.findViewById(R.id.search_bar);
        recyclerView = view.findViewById(R.id.model_objects_with_image);
        positiveButton = view.findViewById(R.id.positive_button);
        negativeButton = view.findViewById(R.id.negative_button);

        setupList();
        setupUIForMode();
    }

    private void setupList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = chooseAdapter();
        recyclerView.setAdapter(adapter);
    }

    private Adapter<? extends ViewHolder> chooseAdapter() {
        switch (mode) {
            case CONFIRMATION:
                return createSimpleAdapter();
            case SINGLE:
                return createSingleAdapter();
            case MULTI:
                return createMultiAdapter();
            case MULTI_LIMIT:
                return createConstraintMultiAdapter();
            default:
                return null;
        }
    }

    private SimpleAdapter<T> createSimpleAdapter() {
        return new SimpleAdapter<>(getContext(),
                R.layout.listitem_model_object_with_image_detail_small,
                R.id.root, objects,
                comparing(T::getName, String::compareToIgnoreCase),
                contentBinder);
    }

    private ContentBinder<T> createBinder() {
        return (parent, element) -> {
            showImage(parent, element);
            showName(parent, element);
        };
    }

    private void showImage(ViewGroup parent, T element) {
        ImageView imageView = parent.findViewById(R.id.show_image);
        imageView.setImageDrawable(
                element.getImage(getContext(), ImagePyramid.ImageSize.LARGE));
    }

    private void showName(ViewGroup parent, T element) {
        TextView nameView = parent.findViewById(R.id.dialog_title);
        nameView.setText(element.getName());
    }

    private SelectionAdapter<T> createSingleAdapter() {
        return singleSelection(getContext(), this::runSingleSelect)
                .setElementLayoutId(listItemLayoutId).setData(objects)
                .setFilterCriterion(filterCriterion).setOrderCriterion(
                        comparing(T::getName, String::compareToIgnoreCase))
                .setContentBinder(contentBinder).build();
    }

    private void runSingleSelect(T element) {
        selectedObjects.clear();
        if (element != null) {
            selectedObjects.add(element);
            setPositiveEnabled(true);
        } else {
            setPositiveEnabled(false);
        }
    }

    private SelectionAdapter<T> createMultiAdapter() {
        return multiSelection(getContext(), createSelectionListener())
                .setElementLayoutId(listItemLayoutId).setData(objects)
                .setFilterCriterion(filterCriterion).setOrderCriterion(
                        comparing(T::getName, String::compareToIgnoreCase))
                .setContentBinder(contentBinder).build();
    }

    private SelectionAdapter<T> createConstraintMultiAdapter() {
        return multiSelection(getContext(), createSelectionListener())
                .setElementLayoutId(listItemLayoutId).setData(objects)
                .setFilterCriterion(filterCriterion).setOrderCriterion(
                        comparing(T::getName, String::compareToIgnoreCase))
                .setContentBinder(contentBinder)
                .setSelectionLimit(selectionLimit).build();
    }

    private MultiSelectionListener<T> createSelectionListener() {
        return new MultiSelectionListener<T>() {
            @Override
            public void onElementSelected(T element) {
                selectedObjects.remove(element);
                selectedObjects.add(element);
                decidePositiveClickMode();
            }

            @Override
            public void onElementUnselected(T element) {
                selectedObjects.remove(element);
                decidePositiveClickMode();
            }
        };
    }

    private void decidePositiveClickMode() {
        if (!selectedObjects.isEmpty()) {
            positiveButton.setOnClickListener(v -> {
                positiveListener.accept(selectedObjects);
                dismiss();
            });
            setPositiveEnabled(true);
            positiveButton.setText(positiveText);
        } else {
            setPositiveEnabled(false);
        }
    }

    private void setPositiveEnabled(boolean enabled) {
        int colorId = enabled ? R.color.white : R.color.lighter_gray;

        positiveButton.setTextColor(getColor(colorId));
        positiveButton.setEnabled(enabled);
    }

    private int getColor(@ColorRes int color) {
        return ofNullable(getContext()).map(context -> context.getColor(color))
                .orElse(0xFFFFFFFF);
    }

    private void setupUIForMode() {
        showTitle.setText(title);
        showMessage.setText(message);
        positiveButton.setText(positiveText);
        negativeButton.setText(negativeText);
        setupSearch();
        setupButtons();

        if (message.trim().isEmpty()) {
            showMessage.setVisibility(View.GONE);
            root.findViewById(R.id.message_divider).setVisibility(View.GONE);
        }
        root.invalidate();
    }

    private void setupSearch() {
        if (mode == Mode.CONFIRMATION) {
            search.setVisibility(View.GONE);
        } else {
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String constraint) {
                    return onQueryTextSubmit(constraint);
                }

                @Override
                public boolean onQueryTextSubmit(String constraint) {
                    decidePositiveClickMode();
                    ((Filterable) adapter).getFilter().filter(constraint);
                    return true;
                }
            });
        }
    }

    private void setupButtons() {
        positiveButton.setOnClickListener(v -> {
            positiveListener.accept(selectedObjects);
            dismiss();
        });
        negativeButton.setOnClickListener(v -> {
            negativeListener.run();
            dismiss();
        });

        setPositiveEnabled(mode == Mode.CONFIRMATION);
    }

    /**
     * Determines whether the dialog shows a list with single selection,
     * multiple selection, or only confirmation (i.e. no list item selection)
     * possibility.
     */
    public enum Mode {
        SINGLE, MULTI, MULTI_LIMIT, CONFIRMATION
    }

    /**
     * Builder class to create a {@link ListDialog}, uses the Builder design
     * pattern. The Builder design pattern allows us to write readable,
     * understandable code to set up complex objects.
     *
     * @param <T> {@link Movie}, {@link Performer}, {@link Tag} or {@link
     *            Renter} class.
     */
    public static class ListDialogBuilder<T extends ModelObjectWithImage> {
        private final FragmentActivity context;
        private Mode mode;
        private String title;
        private String message;
        private int listItemLayoutId;
        private ContentBinder<T> contentBinder;
        private String positiveText;
        private String negativeText;
        private List<T> objects;
        private Integer selectionLimit;
        private BiPredicate<T, CharSequence> filterCriterion;
        private Consumer<List<T>> positiveListener;
        private Runnable negativeListener;

        public ListDialogBuilder(@NonNull FragmentActivity context) {
            this.context = context;
            mode = Mode.SINGLE;
            objects = new ArrayList<>();
            title = context.getString(R.string.dialog_title);
            message = "";
            positiveText = context.getString(R.string.ok);
            negativeText = context.getString(R.string.cancel);
            selectionLimit = -1;
            positiveListener = li -> {
            };
            negativeListener = () -> {
            };
        }

        /**
         * Determines whether the dialog shows a list with single selection,
         * multiple selection, or only confirmation (i.e. no list item
         * selection) possibility.
         *
         * @param mode {@link Mode} is either SINGLE, MULTI, or CONFIRMATION.
         * @return Builder object that can be further extended, e.g. with method
         * {@link ListDialogBuilder#setItems}.
         */
        public ListDialogBuilder<T> setMode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public ListDialogBuilder<T> setTitle(@StringRes int id) {
            return setTitle(context.getString(id));
        }

        public ListDialogBuilder<T> setTitle(String title) {
            this.title = title;
            return this;
        }

        public ListDialogBuilder<T> setMessage(@StringRes int id,
                                               Object... objects) {
            return setMessage(context.getString(id, objects));
        }

        public ListDialogBuilder<T> setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Sets the items/elements/objects to be shown.
         *
         * @param objects list of {@link Movie}, {@link Performer}, {@link Tag}
         *                or {@link Renter} objects.
         * @return Builder object that can be further extended, e.g. with method
         * {@link ListDialogBuilder#setTitle(String)}.
         */
        public ListDialogBuilder<T> setItems(List<T> objects) {
            this.objects = objects;
            return this;
        }

        public ListDialogBuilder<T> setSelectionLimit(Integer selectionLimit) {
            this.selectionLimit = selectionLimit;
            return this;
        }

        public ListDialogBuilder<T> setFilterCriterion(
                BiPredicate<T, CharSequence> filterCriterion) {
            this.filterCriterion = filterCriterion;
            return this;
        }

        public ListDialogBuilder<T> setPositive(@StringRes int text,
                                                Consumer<List<T>> listener) {
            return setPositive(context.getString(text), listener);
        }

        public ListDialogBuilder<T> setPositive(String text,
                                                Consumer<List<T>> listener) {
            positiveText = text;
            positiveListener = listener;
            return this;
        }

        public ListDialogBuilder<T> setNegative(@StringRes int text,
                                                Runnable listener) {
            return setNegative(context.getString(text), listener);
        }

        public ListDialogBuilder<T> setNegative(String text,
                                                Runnable listener) {
            negativeText = text;
            negativeListener = listener;
            return this;
        }

        public ListDialogBuilder<T> setListItemLayoutId(@LayoutRes int layoutId,
                                                        ContentBinder<T> contentBinder) {
            listItemLayoutId = layoutId;
            this.contentBinder = Objects.requireNonNull(contentBinder);
            return this;
        }

        /**
         * Shows the {@link ListDialog}.
         */
        public void show() {
            ListDialog<T> dialog = new ListDialog<>();
            dialog.mode = mode;
            dialog.title = title;
            dialog.message = message;
            dialog.selectionLimit = selectionLimit;
            dialog.filterCriterion = filterCriterion;
            dialog.positiveText = positiveText;
            dialog.positiveListener = positiveListener;
            dialog.negativeText = negativeText;
            dialog.negativeListener = negativeListener;
            dialog.objects = objects;
            if (contentBinder != null) {
                dialog.listItemLayoutId = listItemLayoutId;
                dialog.contentBinder = contentBinder;
            }
            FragmentManager fragmentManager = context
                    .getSupportFragmentManager();
            dialog.show(fragmentManager, "list_dialog_" + mode);
        }
    }
}