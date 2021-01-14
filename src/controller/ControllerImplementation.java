package controller;

import adressbook.model.ABModel;
import adressbook.model.PersonEntry;
import controller.FileHandler.FileHandler;
import model.elements.ArtPieceEntry;
import model.Model;
import view.Views;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Der allgemeine Controller des Werkverzeichnises.
 */

public class ControllerImplementation implements Controller {
    private Model model;
    private FileHandler fileHandler;
    private List<Views> views;
    private List<ArtPieceEntry> selectedElements;

    public ControllerImplementation(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        try {
            this.model = fileHandler.load();
        } catch (IOException e) {
            System.out.println("Konnte Datei nicht laden.");
        } catch (ClassNotFoundException e) {
            System.out.println("Klasse nicht akzeptiert.");
        }
        System.out.println("model übergeben");
        this.views = new ArrayList<>();
        this.selectedElements = new ArrayList<>();
        System.out.println("ControllerImplementation : SelectedElements nach Konstruktor = " + selectedElements);
    }


    @Override
    public boolean isASelectedElement(ArtPieceEntry artPiece) {
        if (artPiece == null || selectedElements == null){
            return false;
        }
        return selectedElements.contains(artPiece);
    }

    @Override
    public void addSelectedElement(ArtPieceEntry artPieceEntry) {
        if (artPieceEntry != null) {
            selectedElements.add(artPieceEntry);
        }
        informViewsSelectedElementsChanged();
    }

    @Override
    public void setSelectedElementTo(ArtPieceEntry artPieceEntry) {
        this.selectedElements = new ArrayList<>();
        selectedElements.add(artPieceEntry);
        informViewsSelectedElementsChanged();
    }

    @Override
    public void modifyEntry(ArtPieceEntry entry, Image imageToLink) {
        ArtPieceEntry entryToChange = model.getEntryWithId(entry.getId());
        entryToChange.setVariablesTo(entry);
        if (imageToLink != null) {
            entryToChange.setBitmap(imageToLink.getScaledInstance(150,150,Image.SCALE_DEFAULT));
            new Thread(() -> fileHandler.saveCopyOfPictureLinkedTorArtpiece(entryToChange.getId(), imageToLink)).start();
        }
        refreshViews();
        //TODO ungetestet
    }

    private void refreshViews() {
        for (Views view : views) {view.refreshView(); }
    }

    @Override
    public void addEntry(ArtPieceEntry entry, Image imageToLink) {
        if (entry.getId() == -1) { entry.setId(createUnusedID());}

        //TODO TEST LÖSCHEN WENN LANGE GENUG GETESTET
        assert (model.getEntryWithId(entry.getId()) != null);

        if (imageToLink != null) {
            entry.setBitmap(imageToLink.getScaledInstance(150,150, Image.SCALE_DEFAULT));
            new Thread ( () -> fileHandler.saveCopyOfPictureLinkedTorArtpiece(entry.getId(), imageToLink));
        } else {
            entry.setBitmap(PictureController.defaultEmptyImage());
        }
        model.getPieces().add(entry);
        refreshViews();
    }


    private int createUnusedID() {
        int id = 0;
        while(model.getEntryWithId(id) != null){
            id++;
        }
        return id;
    }

    @Override
    public PersonEntry getPersonWithIDFromAddressBook(int buyerID) {
        return model.getPersonWithIDFromAdressBook(buyerID);
    }

    @Override
    public void addView(Views view) {
            this.views.add(view);
            view.setModelTo(model);
            view.refreshView();
    }

    public void informViewsSelectedElementsChanged(){
        for (Views view : views) {
            view.changeSelectedElements();
        }
    }

    @Override
    public ABModel getAddressbook() {
        return model.adressbook;
    }

}
