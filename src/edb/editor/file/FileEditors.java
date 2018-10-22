package edb.editor.file;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.ContextMenuHandler;
import com.teamdev.jxbrowser.chromium.ContextMenuParams;
import com.teamdev.jxbrowser.chromium.DefaultLoadHandler;
import com.teamdev.jxbrowser.chromium.LoadParams;
import com.teamdev.jxbrowser.chromium.LoggerProvider;
import com.teamdev.jxbrowser.chromium.PageMargins;
import com.teamdev.jxbrowser.chromium.PaperSize;
import com.teamdev.jxbrowser.chromium.PrintHandler;
import com.teamdev.jxbrowser.chromium.PrintJob;
import com.teamdev.jxbrowser.chromium.PrintSettings;
import com.teamdev.jxbrowser.chromium.PrintStatus;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import ast.actors.Act;
import ast.binding.FunBind;
import ast.binding.declarations.DeclaringLocation;
import ast.modules.Module;
import ast.tests.BArm;
import edb.editor.ClosablePanel;
import edb.editor.HTMLViewer;
import edb.editor.TabbedActor;
import edb.editor.esl.ESLEditor;
import edb.editor.filmstrip.Filmstrip;
import edb.editor.javaold.JavaEditor;
import edb.editor.query.QueryEditor;
import edb.tool.DelayedString;
import edb.tool.EDB;
import runtime.actors.Actor;
import runtime.data.Term;

public class FileEditors extends EditorTabbedPane {

	static JFileChooser fileChooser = new JFileChooser();

	public static String pathToLabel(String path) {
		if (path.endsWith(".esl")) return path.substring(path.lastIndexOf('/') + 1);
		if (path.endsWith(".html")) return path.substring(path.lastIndexOf('/') + 1);
		return path;
	}

	EDB edb;

	public FileEditors(EDB edb) {
		this.edb = edb;
		LoggerProvider.setLevel(Level.OFF);
	}

	private void createNewBrowser(String label, String html, EDB gui) {
		Browser browser = new Browser();
		browser.setZoomEnabled(true);
		BrowserView view = new BrowserView(browser);
		view.setInheritsPopupMenu(true);
		view.setFocusable(true);
		Action printAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				browser.print();
			}
		};
		view.registerKeyboardAction(printAction, KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK), JComponent.WHEN_FOCUSED);
		browser.setPrintHandler(new PrintHandler() {
			public PrintStatus onPrint(PrintJob printJob) {
				PrintSettings settings = printJob.getPrintSettings();
				settings.setPaperSize(PaperSize.ISO_A4);
				settings.setLandscape(false);
				settings.setPrintToPDF(true);
				settings.setPageMargins(new PageMargins(0, 0, 0, 0, 0, 0));
				settings.setLandscape(true);
				if (fileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					settings.setPDFFilePath(file.getAbsolutePath());
					return PrintStatus.CONTINUE;
				} else
					return PrintStatus.CANCEL;
			}
		});
		browser.setLoadHandler(new DefaultLoadHandler() {
			public boolean onLoad(LoadParams params) {
				if (params.getURL().startsWith("edb:")) {
					String message = params.getURL().substring(4);
					edb.browserEvent(message);
					return false;
				} else
					return true;
			}
		});
		view.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				edb.setFocus(view);
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
				edb.setFocus(view);
			}

			public void mouseReleased(MouseEvent e) {
				if (EDB.getOsflag().startsWith("Win")) edb.setFocus(view);
			}

		});
		view.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				edb.setFocus(view);
			}

			public void focusLost(FocusEvent e) {
			}
		});
		view.setComponentPopupMenu(popup(browser));
		browser.loadHTML(html);
		addTab(label, view);
	}

	private void createNewFilmstrip(String label, Vector<DelayedString> htmls, EDB gui) {
		Filmstrip filmstrip = new Filmstrip(htmls);
		JScrollPane scroller = new JScrollPane(filmstrip);
		// scroller.setComponentPopupMenu(popup(filmstrip));
		addTab(label, scroller);
	}

	private void createPanel(String label, JPanel panel) {
		ClosablePanel parent = new ClosablePanel(panel) {
			public void close() {
				if (panel instanceof TabbedActor) {
					TabbedActor closable = (TabbedActor) panel;
					closable.close();
				}
			}

			public void deselect() {
				if (panel instanceof TabbedActor) {
					TabbedActor closable = (TabbedActor) panel;
					closable.deselect();
				}
			}

			public void select() {
				if (panel instanceof TabbedActor) {
					TabbedActor closable = (TabbedActor) panel;
					closable.select();
				}
			}
		};
		parent.setLayout(new BorderLayout());
		parent.add(panel);
		addTab(label, parent);
	}

	public void error(String path, int charPos, String error) {
		int i = indexOfTab(path);
		if (i != -1) {
			getFileEditor(getComponentAt(i)).error(charPos, error);
		}
	}

	public void fileDeleted(String path) {
		int i = indexOfTab(path);
		if (i != -1) this.remove(i);
	}

	private FileEditor getFileEditor(Component component) {
		if (component instanceof JScrollPane) {
			JScrollPane scroller = (JScrollPane) component;
			return (FileEditor) scroller.getViewport().getView();
		} else if (component instanceof EditorContainer) {
			EditorContainer container = (EditorContainer) component;
			return container.getEditor();
		} else
			throw new Error("cannot get an editor from " + component);
	}

	private FileEditor getSelectedFileEditor() {
		Component component = getSelectedComponent();
		if (isFileEditorContainer(component)) {
			return getFileEditor(component);
		} else
			return null;
	}

	private Filmstrip getSelectedFilmstrip() {
		Component component = getSelectedComponent();
		if (component != null && component instanceof JScrollPane) {
			JScrollPane scroller = (JScrollPane) component;
			return (Filmstrip) scroller.getViewport().getView();
		} else
			return null;
	}

	public void hasError(String path, boolean isError) {
		int i = indexOfTab(path);
		if (i != -1) {
			CloseButtonTab tab = (CloseButtonTab) getTabComponentAt(i);
			tab.hasError(isError);
		}
	}

	public boolean isBreakpoint(Actor actor, int line) {
		// Needs updating due to new editor.
		int i = indexOfTab(actor.getCode().getPath());
		if (i != -1) {
			Component component = getComponentAt(i);
			return false;
		} else
			return false;
	}

	public boolean isDirty(String path) {
		int i = indexOfTab(path);
		if (i != -1) {
			FileEditor editor = getFileEditor(getComponentAt(i));
			return editor.isDirty();
		}
		return false;
	}

	private boolean isFileEditorContainer(Component component) {
		return component != null && (component instanceof JScrollPane || component instanceof EditorContainer);
	}

	public void join(FileEditor editor) {
		String tab = editor.getPath();
		int index = indexOfTab(tab);
		if (index != -1) {
			Component component = getComponentAt(index);
			if (component instanceof EditorContainer) {
				EditorContainer container = (EditorContainer) component;
				container.join();
			}
		}
	}

	private void loadESLFile(String path, EDB gui) {
		ESLEditor editor = new ESLEditor(path, gui, 50, 50);
		addTab(path, editor);
		Module module = editor.getModule(false);
		if (module != null) gui.displayTree(module);
	}

	private void loadFile(String path, EDB gui) {
		if (path.startsWith("http"))
			loadURL(path, gui);
		else if (path.endsWith(".pdf"))
			loadURL(path, gui);
		else if (path.endsWith(".flm"))
			loadFilmstrip(path, gui);
		else if (path.endsWith(".esl"))
			loadESLFile(path, gui);
		else if (path.endsWith(".html"))
			loadHTMLFile(path, gui);
		else if (path.endsWith(".q"))
			loadQueryFile(path, gui);
		else if (path.endsWith(".r"))
			loadQueryFile(path, gui);
		else if (path.endsWith(".java"))
			loadJavaFile(path, gui);
		else
			System.out.println("FileEditors.loadFile: unknown type of file: " + path);
	}

	private void loadFilmstrip(String path, EDB edb) {

		// A filmstrip is a serialized term of the form: Filmstrip(Str,[EDBDisplay])
		// Inflate it and display it...

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				FileInputStream fin;
				try {
					System.err.println("[ loading filmstrip " + path + " ]");
					fin = new FileInputStream(path);
					ObjectInputStream in = new ObjectInputStream(fin);
					Object o = in.readObject();
					in.close();
					if (o != null && o instanceof Term) {
						System.err.println("[ got filmstrip " + path + " ]");
						Term filmstrip = (Term) o;
						edb.send(filmstrip, 0);
					} else
						throw new Error("expecting a filmstrip " + o);
				} catch (IOException | ClassNotFoundException e) {
					throw new Error(e.toString());
				}
			}
		});

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

	private void loadJavaFile(String path, EDB gui) {
		JavaEditor editor = new JavaEditor(path, gui);
		JScrollPane scroller = new JScrollPane(editor);
		scroller.setRowHeaderView(editor.getLines());
		editor.parseText();
		addTab(path, scroller);
	}

	private void loadQueryFile(String path, EDB gui) {
		QueryEditor editor = new QueryEditor(path, gui);
		JScrollPane scroller = new JScrollPane(editor);
		scroller.setRowHeaderView(editor.getLines());
		editor.parseText();
		addTab(path, scroller);
	}

	private void loadURL(String path, EDB gui) {
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		new Thread(new Runnable() {
			public void run() {
				Browser browser = new Browser();
				BrowserView view = new BrowserView(browser);
				browser.setContextMenuHandler(new ContextMenuHandler() {
					public void showContextMenu(ContextMenuParams params) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								JPopupMenu popup = popup(browser);
								if (browser.canGoBack()) {
									JMenuItem back = new JMenuItem("Back");
									back.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											browser.goBack();
										}
									});
									popup.add(back);
								}
								if (browser.canGoForward()) {
									JMenuItem forward = new JMenuItem("Forward");
									forward.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											browser.goForward();
										}
									});
									popup.add(forward);
								}
								popup.show(view, params.getLocation().x, params.getLocation().y);
							}
						});
					}
				});
				browser.loadURL(path);

				addTab(path, view);
				setSelectedIndex(indexOfTab(path));
			}

		}).start();

	}

	public void openFile(String path, EDB gui) {
		int i = indexOfTab(path);
		if (i == -1) loadFile(path, gui);
		i = indexOfTab(path);
		setSelectedIndex(i);
	}

	public void openPanel(JPanel panel) {
		String label = "Panel(" + panel.hashCode() + ")";
		int i = indexOfTab(label);
		if (i == -1) createPanel(label, panel);
		i = indexOfTab(label);
		setSelectedIndex(i);
	}

	private JPopupMenu popup(Browser browser) {
		JPopupMenu popup = new JPopupMenu();
		JMenuItem print = new JMenuItem("Print");
		print.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browser.print();
			}
		});
		popup.add(print);
		return popup;
	}

	public void resizeFont(int amount) {
		int i = getSelectedIndex();
		if (i != -1) getSelectedFileEditor().resizeFont(amount);
	}

	public void run() {
		FileEditor editor = getSelectedFileEditor();
		if (editor != null) editor.run();
	}

	public void selectLine(int line) {
		FileEditor editor = getSelectedFileEditor();
		if (editor != null) editor.selectLine(line);
	}

	public void showDeclaration(String path, DeclaringLocation dec) {
		int i = indexOfTab(path);
		if (i == -1) {
			loadFile(path, edb);
			i = indexOfTab(path);
		}
		setSelectedIndex(i);
		FileEditor editor = (FileEditor) getSelectedFileEditor();
		int index = dec.getLineStart();
		editor.showTextAt(index);
	}

	public synchronized void showHTML(String label, String html, EDB gui) {
		int i = indexOfTab(label);
		if (i == -1)
			createNewBrowser(label, html, gui);
		else {
			setSelectedIndex(i);
			Component c = getSelectedComponent();
			if (c instanceof BrowserView) {
				BrowserView v = (BrowserView) c;
				Browser b = v.getBrowser();
				b.loadHTML(html);
				v.repaint();
			}
		}
		setSelectedIndex(indexOfTab(label));
	}

	public void showHTML(String label, Vector<DelayedString> htmls, EDB gui) {
		int i = indexOfTab(label);
		if (i == -1)
			createNewFilmstrip(label, htmls, gui);
		else {
			setSelectedIndex(i);
			Filmstrip filmstrip = getSelectedFilmstrip();
			if (filmstrip == null)
				createNewFilmstrip(label, htmls, gui);
			else
				filmstrip.setFilmstrip(htmls);
		}
		setSelectedIndex(indexOfTab(label));
	}

	public void split(FileEditor editor) {
		String tab = editor.getPath();
		int index = indexOfTab(tab);
		if (index != -1) {
			Component component = getComponentAt(index);
			if (component instanceof EditorContainer) {
				EditorContainer container = (EditorContainer) component;
				container.split();
			}
		}
	}

	public Vector<Act> tracedActs(String path) {
		int i = indexOfTab(path);
		if (i != -1) {
			// ESLEditor editor = (ESLEditor) getFileEditor(getComponentAt(i));
			// return editor.getTracedActs();
		}
		return new Vector<Act>();
	}

	public Vector<BArm> tracedArms(String path) {
		int i = indexOfTab(path);
		if (i != -1) {
			// ESLEditor editor = (ESLEditor) getFileEditor(getComponentAt(i));
			// return editor.getTracedArms();
		}
		return new Vector<BArm>();
	}

	public Vector<FunBind> tracedFuns(String path) {
		int i = indexOfTab(path);
		if (i != -1) {
			// ESLEditor editor = (ESLEditor) getFileEditor(getComponentAt(i));
			// return editor.getTracedFuns();
		}
		return new Vector<FunBind>();
	}

}