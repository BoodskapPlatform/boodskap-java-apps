package io.boodskap.iot.wb;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterChoicesProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

public class EditorComponent extends JPanel {
	
	private static final long serialVersionUID = 6409486987390740548L;
	
	private final TreeMenuPanel viewPanel;
	private final TreeMenu menu;
	private final RSyntaxTextArea textArea;
	private boolean changed = false;

	public EditorComponent(TreeMenuPanel viewPanel, TreeMenu menu) {
		
		super(new BorderLayout());
		
		this.viewPanel = viewPanel;
		this.menu = menu;
		
		textArea = new RSyntaxTextArea();
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
		textArea.setCodeFoldingEnabled(true);
		
		RTextScrollPane scroll = new RTextScrollPane(textArea);
		
		add(scroll, BorderLayout.CENTER);
		
		textArea.getDocument().addDocumentListener(new DocumentListener() {

	        @Override
	        public void removeUpdate(DocumentEvent e) {
	        	changed = true;
	        }

	        @Override
	        public void insertUpdate(DocumentEvent e) {
	        	changed = true;
	        }

	        @Override
	        public void changedUpdate(DocumentEvent arg0) {
	        	changed = true;
	        }
	    });
		
		setupCompletion();
		
	}
	
	public TreeMenuPanel getViewPanel() {
		return viewPanel;
	}
	
	public TreeMenu getMenu() {
		return menu;
	}
	
	public void load() {
		
		switch(menu.getType()) {
		case DOMAIN_RULE:
			break;
		case GROOVY_CLASS:
			break;
		case MESSAGE_RULES:
			break;
		case NAMED_RULE:
			break;
		case NAMED_RULES:
			break;
		case SCHEDULED_RULE:
			break;
		default:
			break;
		}
		
	}

	protected void setupCompletion() {
		
		Action gotoLineAction = new AbstractAction("gotoline") {
			private static final long serialVersionUID = -3641065859880662889L;
			public void actionPerformed(ActionEvent ae) {
	            String value = getClickedWord(textArea.getText(), textArea.getCaret().getDot());
	            if(null != value && !value.trim().equals("")) {
	            	System.out.println(value);
	            }
	         }
	    };
	    
		textArea.getInputMap().put(KeyStroke.getKeyStroke("PERIOD"), "gotoline");
		textArea.getActionMap().put(gotoLineAction.getValue(Action.NAME), gotoLineAction);
		
		
		CompletionProvider provider = createCompletionProvider();
		AutoCompletion ac = new AutoCompletion(provider);
		ac.install(textArea);
	}

	protected CompletionProvider createCompletionProvider() {

		// A DefaultCompletionProvider is the simplest concrete implementation
		// of CompletionProvider. This provider has no understanding of
		// language semantics. It simply checks the text entered up to the
		// caret position for a match against known completions. This is all
		// that is needed in the majority of cases.
		DefaultCompletionProvider provider = new DefaultCompletionProvider();
		
		provider.setParameterizedCompletionParams('(', ",", ')');

		// Add completions for all Java keywords. A BasicCompletion is just
		// a straightforward word completion.
		provider.addCompletion(new BasicCompletion(provider, "abstract"));
		provider.addCompletion(new BasicCompletion(provider, "assert"));
		provider.addCompletion(new BasicCompletion(provider, "break"));
		provider.addCompletion(new BasicCompletion(provider, "case"));
		// ... etc ...
		provider.addCompletion(new BasicCompletion(provider, "transient"));
		provider.addCompletion(new BasicCompletion(provider, "void"));
		provider.addCompletion(new BasicCompletion(provider, "volatile"));
		provider.addCompletion(new BasicCompletion(provider, "while"));
		provider.addCompletion(new BasicCompletion(provider, "domain"));

		FunctionCompletion fc = new FunctionCompletion(provider, "defineMessage", "void");
		fc.setDefinedIn("domain");
		fc.setShortDescription("Defines a new message");
		fc.setSummary("Defines a new message");
		Parameter p = new Parameter(Integer.class, "messageId", true);
		fc.setParams(Arrays.asList(p));
		provider.addCompletion(fc);
		
		// Add a couple of "shorthand" completions. These completions don't
		// require the input text to be the same thing as the replacement text.
		provider.addCompletion(new ShorthandCompletion(provider, "format", "log.info(\"%s\", \"\");"));
		provider.addCompletion(new ShorthandCompletion(provider, "log", "log.info(\"\");"));
		provider.addCompletion(new ShorthandCompletion(provider, "warn", "log.warn(\"\");"));
		provider.addCompletion(new ShorthandCompletion(provider, "error", "log.error(\"\");"));
		provider.addCompletion(new ShorthandCompletion(provider, "debug", "log.debug(\"\");"));
		provider.addCompletion(new ShorthandCompletion(provider, "try", "try{\n\t\n}catch(Exception ex){\n\tex.printStrackTrace();\n}", "try-catch block"));
		
		
		provider.setParameterChoicesProvider(new ParameterChoicesProvider() {
			@Override
			public List<Completion> getParameterChoices(JTextComponent tc, Parameter param) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		
		return provider;

	}

	protected String getClickedWord(String ocontent, int caretPosition) {
	    try {

	    	String word = "";
	        
	    	if (ocontent.length() == 0 || caretPosition <= 0) {
	            return word;
	        }
	    	
	    	char[] chars = ocontent.toCharArray();
	    	int begin = caretPosition-1;
	    	
	    	for(int i=caretPosition-1;i>=0;i--) {
	    		if (Character.isWhitespace(chars[i])) {
	    		    break;
	    		}
	    		begin = i;
	    	}
	    	
	    	word = ocontent.substring(begin, caretPosition);
	        
	        return word;
	    } catch (StringIndexOutOfBoundsException e) {
	        return "";
	    }
	     
	}
	
	public boolean close() {
		
		if(changed) {
			int option = JOptionPane.showConfirmDialog(this, "Content Changed, do you want to close without saving?", "Content Changed", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if(option == JOptionPane.YES_OPTION) {
				changed = false;
			}
		}
		
		if(!changed) {
			viewPanel.removeView(menu);
		}
		
		return !changed;
	}

}
