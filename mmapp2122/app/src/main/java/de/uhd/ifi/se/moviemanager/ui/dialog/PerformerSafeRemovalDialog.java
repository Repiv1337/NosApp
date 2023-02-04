package de.uhd.ifi.se.moviemanager.ui.dialog;

import static de.uhd.ifi.se.moviemanager.ui.dialog.ListDialog.Mode.CONFIRMATION;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.Performer;

public class PerformerSafeRemovalDialog {

    private PerformerSafeRemovalDialog() {
    }

    public static void showIfNecessary(FragmentActivity context,
                                       List<Performer> performers,
                                       Consumer<List<Performer>> positiveListener,
                                       Runnable negativeListener,
                                       Runnable defaultAction) {
        List<Performer> performersToRemove = getInvalidPerformers(performers);
        if (!performersToRemove.isEmpty()) {
            show(context, performersToRemove, positiveListener,
                    negativeListener);
        } else {
            defaultAction.run();
        }
    }

    public static void show(FragmentActivity context,
                            List<Performer> performers,
                            Consumer<List<Performer>> positiveListener,
                            Runnable negativeListener) {
        List<Performer> performersToRemove = getInvalidPerformers(performers);
        ListDialog.<Performer>builder(context).setMode(CONFIRMATION)
                .setTitle(R.string.warning)
                .setMessage(R.string.warning_linked_performers_removal,
                        performersToRemove.size()).setItems(performersToRemove)
                .setPositive(R.string.yes, li -> positiveListener
                        .accept(li.stream().map(Performer.class::cast)
                                .collect(Collectors.toList())))
                .setNegative(R.string.no, negativeListener).show();
    }

    /**
     * Returns a list of performers without linked movie.
     *
     * @param linkedPerformer
     * @return list of performers without linked movie
     */
    public static List<Performer> getInvalidPerformers(
            List<Performer> linkedPerformer) {
        List<Performer> performersToRemove = new ArrayList<>();
        for (Performer performer : linkedPerformer) {
            if (performer.getMovies().size() <= 1) {
                performersToRemove.add(performer);
            }
        }
        return performersToRemove;
    }
}
