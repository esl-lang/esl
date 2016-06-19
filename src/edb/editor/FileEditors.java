package edb.editor;

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.swing.JScrollPane;

import actors.Actor;
import edb.tool.EDB;

public class FileEditors extends EditorTabbedPane {

  public static String pathToLabel(String path) {
    if (path.endsWith(".esl")) return path.substring(path.lastIndexOf('/') + 1);
    if (path.endsWith(".html")) return path.substring(path.lastIndexOf('/') + 1);
    return path;
  }

  public void fileDeleted(String path) {
    int i = indexOfTab(path);
    if (i != -1) this.remove(i);
    
  }

  private FileEditor getSelectedFileEditor() {
    Component component = getSelectedComponent();
    if (component != null && component instanceof JScrollPane) {
      JScrollPane scroller = (JScrollPane) component;
      return (FileEditor) scroller.getViewport().getView();
    } else return null;
  }

  public boolean isBreakpoint(Actor actor, int line) {
    int i = indexOfTab(actor.getCode().getPath());
    if (i != -1) {
      Component component = getComponentAt(i);
      if (component != null && component instanceof JScrollPane) {
        JScrollPane scroller = (JScrollPane) component;
        FileEditor editor = (FileEditor) scroller.getViewport().getView();

        return editor.isBreakpoint(line);
      } else return false;
    } else return false;
  }

  public boolean isDirty(String path) {
    int i = indexOfTab(path);
    if (i == -1) {
      FileEditor editor = (FileEditor) getComponentAt(i);
      return editor.isDirty();
    }
    return false;
  }

  private void loadESLFile(String path, EDB gui) {
    FileEditor editor = new FileEditor(path, gui);
    JScrollPane scroller = new JScrollPane(editor);
    scroller.setRowHeaderView(editor.getLines());
    addTab(path, scroller);
    editor.parseText();
  }

  private void loadFile(String path, EDB gui) {
    if (path.endsWith(".esl"))
      loadESLFile(path, gui);
    else if (path.endsWith(".html"))
      loadHTMLFile(path, gui);
    else System.out.println("FileEditors.loadFile: unknown type of file: " + path);
    ;
  }

  private void loadHTMLFile(final String path, EDB gui) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String url = new File(path).toURI().toURL().toString();
          HTMLViewer viewer = new HTMLViewer(url, gui);
          JScrollPane scroller = new JScrollPane(viewer);
          addTab(path, scroller);
          setSelectedIndex(indexOfTab(path));
        } catch (IOException ioe) {
          ioe.printStackTrace(System.out);
        }
      }
    }).start();
  }

  public void openFile(String path, EDB gui) {
    int i = indexOfTab(path);
    if (i == -1) loadFile(path, gui);
    i = indexOfTab(path);
    setSelectedIndex(i);
  }

  public void resizeFont(int amount) {
    int i = getSelectedIndex();
    if (i != -1) getSelectedFileEditor().resizeFont(amount);
  }

  public void selectLine(int line) {
    FileEditor editor = getSelectedFileEditor();
    if (editor != null) editor.selectLine(line);
  }

}