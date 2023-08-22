//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.dlsc.gemsfx.skins;

import com.dlsc.gemsfx.SearchField;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class SearchFieldPopup<T> extends PopupControl {

    private final ObservableList<T> suggestions = FXCollections.observableArrayList();
    private final SearchField<T> searchField;

    public static final String DEFAULT_STYLE_CLASS = "search-field-popup";

    private final BooleanProperty shouldCommitProperty;

    public SearchFieldPopup(SearchField<T> searchField, BooleanProperty shouldCommitProperty) {
        this.searchField = Objects.requireNonNull(searchField);
        this.shouldCommitProperty = shouldCommitProperty;

        minWidthProperty().bind(searchField.widthProperty());

        setAutoFix(true);
        setAutoHide(true);
        setHideOnEscape(true);

        getStyleClass().add(DEFAULT_STYLE_CLASS);

        searchField.addEventHandler(SearchField.SearchEvent.SEARCH_FINISHED, evt -> {
            if ((!searchField.getSuggestions().isEmpty() || searchField.getPlaceholder() != null) && StringUtils.isNotBlank(searchField.getEditor().getText())) {

                // assuming that we don't have to show it
                boolean showIt = false;
                if (searchField.getSuggestions().size() == 1) {
                    if (!searchField.isHidePopupWithSingleChoice() || !searchField.getMatcher().apply(searchField.getSuggestions().get(0), evt.getText())) {

                        // code said "show it" even with only a single suggestion
                        // but let's see if the suggestion is identical to the typed text, then we really do not want to show it
                        if (!searchField.getConverter().toString(searchField.getSuggestions().get(0)).equalsIgnoreCase(searchField.getText())) {
                            showIt = true;
                        }
                    }
                } else {
                    // more than one suggested item, definitely show the popup
                    showIt = true;
                }

                if (showIt) {
                    show(searchField);
                    selectFirstSuggestion();
                } else {
                    hide();
                }
            } else {
                hide();
            }
        });
    }

    public SearchField<T> getSearchField() {
        return searchField;
    }

    public ObservableList<T> getSuggestions() {
        return suggestions;
    }

    public void show(Node node) {
        if (node.getScene() != null && node.getScene().getWindow() != null) {
            Window parent = node.getScene().getWindow();
            getScene().setNodeOrientation(node.getEffectiveNodeOrientation());
            if (node.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT) {
                setAnchorLocation(AnchorLocation.CONTENT_TOP_RIGHT);
            } else {
                setAnchorLocation(AnchorLocation.CONTENT_TOP_LEFT);
            }

            show(node, parent.getX() + node.localToScene(0.0D, 0.0D).getX() + node.getScene().getX(), parent.getY() + node.localToScene(0.0D, 0.0D).getY() + node.getScene().getY() + node.getBoundsInParent().getHeight());
        } else {
            throw new IllegalStateException("Can not show popup. The node must be attached to a scene/window.");
        }
    }

    /**
     * Selects the first suggestion (if any), so the user can choose it
     * by pressing enter immediately.
     */
    private void selectFirstSuggestion() {
        SearchFieldPopupSkin<T> skin = (SearchFieldPopupSkin) getSkin();
        ListView<?> listView = (ListView<?>) skin.getNode();
        if (listView.getItems() != null && !listView.getItems().isEmpty()) {
            listView.getSelectionModel().select(0);
        }
    }

    protected Skin<?> createDefaultSkin() {
        return new SearchFieldPopupSkin(this, shouldCommitProperty);
    }
}
