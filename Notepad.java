	import java.awt.*;
	import java.awt.event.*;
	import java.io.*;
	import javax.swing.*;
	import java.util.*;
	import javax.swing.text.DefaultHighlighter;
	import javax.swing.text.Highlighter;
	import javax.swing.text.Highlighter.HighlightPainter;
	import javax.swing.BorderFactory;
	import javax.swing.event.CaretEvent;
    import javax.swing.event.CaretListener;
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;


	class Notepad 
	{
		//Declaring everything onboard
		JFrame f;
		JScrollBar sb;
		JTextArea t, tb;
		JMenuBar mb;
		JMenu File, Edit, Fonts, Emoji;
		JMenuItem Inc, Dec;
		JMenuItem Smiley, Laugh, Cry, Angry;
		JMenuItem New, Open, Save, Save_as, Exit;
		JMenuItem Undo, Cut, Copy, Paste, Delete, Find, Replace;
		String storage;
		String sec_storage;
		

		//Stack for Undo operation
		Stack<String> stack= new Stack<String>();  
		static int pos=0;
		
		//method for updating line and column number at the bottom textarea
		private void updateStatus(int linenumber, int columnnumber)
			 {
        		tb.setText("Line: " + linenumber + " Column: " + columnnumber);
    		 }

    	//Constructor of Notepad class
		public Notepad()
		{
			//Defining frame f and getting its container cp
			f= new JFrame("Notepad Ninja");
			f.setSize(500,500);
			f.setLayout(new FlowLayout());
			Container cp= f.getContentPane();
			cp.setLayout(new BorderLayout());

			//Scrollbar
			sb= new JScrollBar();
			sb.setBounds(500,500,50,100);
			
			//Defining menubar, menu and menuitems and add them in respective menu
			mb= new JMenuBar();
			File= new JMenu("File");
			File.setFont(new Font("sans-serif", Font.PLAIN, 20));
			Edit= new JMenu("Edit");
			Edit.setFont(new Font("sans-serif", Font.PLAIN, 20));
			Fonts= new JMenu("Font");
			Fonts.setFont(new Font("sans-serif", Font.PLAIN, 20));
			Emoji= new JMenu("Emoji");					
			Emoji.setFont(new Font("sans-serif", Font.PLAIN, 20));
			

			Inc= new JMenuItem("Inc");					Inc.setFont(new Font("sans-serif", Font.PLAIN, 15));
			Dec= new JMenuItem("Dec");					Dec.setFont(new Font("sans-serif", Font.PLAIN, 15));
			Fonts.add(Inc);
			Fonts.add(Dec);
			
			New= new JMenuItem("New");                  New.setFont(new Font("sans-serif", Font.PLAIN, 15));
			Open= new JMenuItem("Open");                Open.setFont(new Font("sans-serif", Font.PLAIN, 15));
			Save= new JMenuItem("Save");                Save.setFont(new Font("sans-serif", Font.PLAIN, 15));
			Save_as= new JMenuItem("Save as");          Save_as.setFont(new Font("sans-serif", Font.PLAIN, 15));
			Exit= new JMenuItem("Exit");                Exit.setFont(new Font("sans-serif", Font.PLAIN, 15));

			File.add(New);
			File.add(Open);
			File.add(Save);
			File.add(Save_as);
			File.add(Exit);
			

			Undo= new JMenuItem("Undo");				Undo.setFont(new Font("sans-serif", Font.PLAIN, 15));
			Cut= new JMenuItem("Cut");					Cut.setFont(new Font("sans-serif", Font.PLAIN, 15));
			Copy= new JMenuItem("Copy");				Copy.setFont(new Font("sans-serif", Font.PLAIN, 15));
			Paste= new JMenuItem("Paste");				Paste.setFont(new Font("sans-serif", Font.PLAIN, 15));
			Delete= new JMenuItem("Delete");			Delete.setFont(new Font("sans-serif", Font.PLAIN, 15));
			Find= new JMenuItem("Find");				Find.setFont(new Font("sans-serif", Font.PLAIN, 15));
			Replace= new JMenuItem("Replace");			Replace.setFont(new Font("sans-serif", Font.PLAIN, 15));
			

			Edit.add(Undo);
			Edit.add(Cut);
			Edit.add(Copy);
			Edit.add(Paste);
			Edit.add(Delete);
			Edit.add(Find);
			Edit.add(Replace);

			//Emojis		
			Smiley= new JMenuItem("\u263a");			Smiley.setFont(new Font("sans-serif", Font.PLAIN, 15));
			Laugh= new JMenuItem("\uD83D\uDE02");		Laugh.setFont(new Font("sans-serif", Font.PLAIN, 15));
			Cry= new JMenuItem("\uD83D\uDE2D");			Cry.setFont(new Font("sans-serif", Font.PLAIN, 15));
			Angry= new JMenuItem("\uD83D\uDE21");		Angry.setFont(new Font("sans-serif", Font.PLAIN, 15));
			
			Emoji.add(Smiley);
			Emoji.add(Laugh);
			Emoji.add(Cry);
			Emoji.add(Angry);



			mb.setSize(600, 400);
			mb.add(File);
			mb.add(Edit);
			mb.add(Fonts);
			mb.add(Emoji);
			

			

			t= new JTextArea(10, TextArea.SCROLLBARS_VERTICAL_ONLY);
			tb= new JTextArea(1, 10); // bottom textarea showing no. of line and column
			cp.add(sb,BorderLayout.EAST); //Scrollbar vertical in east direction
			cp.add(t); //takes the remaining space
			cp.add(tb, BorderLayout.SOUTH); //bottom south

			//Setting the border for the bottom textarea
   			tb.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			f.setJMenuBar(mb);
			f.addWindowListener(new OverriddenClass());
			updateStatus(1,1);

			//Event handling for line and column no. count
			t.addCaretListener(new CaretListener() {
            // Each time the caret is moved, it will trigger the listener and its method caretUpdate.
            // It will then pass the event to the update method including the source of the event (which is our textarea control)
            public void caretUpdate(CaretEvent e) {
                JTextArea editArea = (JTextArea)e.getSource();

                // Lets start with some default values for the line and column.
                int linenum = 1;
                int columnnum = 1;

                // We create a try catch to catch any exceptions. We will simply ignore such an error for our demonstration.
                try {
                    // First we find the position of the caret. This is the number of where the caret is in relation to the start of the JTextArea
                    // in the upper left corner. We use this position to find offset values (eg what line we are on for the given position as well as
                    // what position that line starts on.
                    int caretpos = editArea.getCaretPosition();
                    linenum = editArea.getLineOfOffset(caretpos);

                    // We subtract the offset of where our line starts from the overall caret position.
                    // So lets say that we are on line 5 and that line starts at caret position 100, if our caret position is currently 106
                    // we know that we must be on column 6 of line 5.
                    columnnum = caretpos - editArea.getLineStartOffset(linenum);

                    // We have to add one here because line numbers start at 0 for getLineOfOffset and we want it to start at 1 for display.
                    linenum += 1;
                }
                catch(Exception ex) { }

                // Once we know the position of the line and the column, pass it to a helper function for updating the status bar.
                updateStatus(linenum, columnnum);
            }
        });
			//firing an event for everything in notepad ninja
			Save.addActionListener(new OverriddenClass());
			Save_as.addActionListener(new OverriddenClass());
			Exit.addActionListener(new OverriddenClass());
			Open.addActionListener(new OverriddenClass());
			Copy.addActionListener(new OverriddenClass());
			Cut.addActionListener(new OverriddenClass());
			Paste.addActionListener(new OverriddenClass());
			New.addActionListener(new OverriddenClass());
			Inc.addActionListener(new OverriddenClass());
			Dec.addActionListener(new OverriddenClass());
			Delete.addActionListener(new OverriddenClass());
			Undo.addActionListener(new OverriddenClass());
			Find.addActionListener(new OverriddenClass());
			Replace.addActionListener(new OverriddenClass());
			Smiley.addActionListener(new OverriddenClass());
			Laugh.addActionListener(new OverriddenClass());
			Cry.addActionListener(new OverriddenClass());
			Angry.addActionListener(new OverriddenClass());
			

			f.setVisible(true);
		}

		//Nested class OverriddenClass which implements the interfaces used and handling every event
		class OverriddenClass implements WindowListener, ActionListener
		{
			
			String location;
			
			
			

					JFrame f,f1;
					JDialog d,d1;
					JLabel what_to_find;
					JTextField ip;
					JButton find;
					JButton cancel;
					JButton revert;
					JLabel replace_with;
	    			JButton replace;
	    			JTextField replace_what, replacable;

			//For Red cross
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
			public void windowOpened(WindowEvent evt) { }
	    	public void windowClosed(WindowEvent evt) { }
	    	public void windowIconified(WindowEvent evt) { }
	    	public void windowDeiconified(WindowEvent evt) { }
	    	public void windowActivated(WindowEvent evt) { }
	    	public void windowDeactivated(WindowEvent evt) { }

	    	public void actionPerformed(ActionEvent e)
	    	{
	    		String menuLabel= e.getActionCommand();

	    		//For save as operation
		    		if(menuLabel.equals("Save as"))
		    		{
		    			JFileChooser save= new JFileChooser();
		    			int option= save.showSaveDialog(Save_as);
		    			try{
		    			if(option==JFileChooser.APPROVE_OPTION)
		    			{
		    				location= save.getSelectedFile().getPath();
		    				BufferedWriter out = new BufferedWriter(new FileWriter(save.getSelectedFile().getPath()));
							out.write(t.getText());
							out.close();
		    			}
		    		}
		    		catch(Exception et)
		    		{
		    			System.out.println(et.getMessage());
		    		}
		    		}

		    		//for save operation
	    		else if(menuLabel.equals("Save"))
	    		{
	    			try{
	    			
	    				BufferedWriter out = new BufferedWriter(new FileWriter(location));
						out.write(t.getText());
						out.close();
	    			}
	    		
	    		catch(Exception et)
	    		{
	    			System.out.println(et.getMessage());
	    		}
	    		}

	    		//for exit

	    		else if(menuLabel.equals("Exit"))
	    		{
	    			System.exit(0);
	    		}
	    		//for opening a new file

	    		else if(menuLabel.equals("Open"))
	    		{
	    			JFileChooser open= new JFileChooser();
	    			int option= open.showOpenDialog(Open);

	    				t.setText("");
	    			try{
	    			if(option==JFileChooser.APPROVE_OPTION)
	    			{
	    				Scanner scan= new Scanner(new FileReader(open.getSelectedFile().getPath()));
	    				while(scan.hasNext())
	    				{
	    					t.append(scan.nextLine()+"\n");
	    				}
	    			}
	    		}
	    		catch(Exception et)
	    		{
	    			System.out.println(et.getMessage());
	    		}

	    		}
	    		// for copying the selected elements
	    		else if(e.getActionCommand()=="Copy")
	    		{
	    		if(t.getSelectedText()!=null)
	    		{
	    			storage= t.getSelectedText();
	    		}

	    		System.out.println(storage+" copy");
	    	}

	    	//for paste operation
	    	else if(e.getActionCommand()=="Paste")
	    		{
	    			System.out.println(storage);
	    			System.out.println("paste");
	    			int p = t.getCaretPosition();
	    			
	    			t.insert(storage,p);
	    		
	    		}

	    		//for cut the selected content

	    		else if(e.getActionCommand()=="Cut")
	    		{
	    			if(t.getSelectedText()!=null)
	    		{
	    			storage= t.getSelectedText();

	    		}
	    			t.setText(t.getText().replace(t.getSelectedText(),""));


	    		}

	    		//for starting a new file
	    		else if(e.getActionCommand()=="New")
	    		{
	    			t.setText ("");

	    		}

	    		//Incrementing the font size of text
	    		else if(e.getActionCommand()=="Inc")
	    		{
	    			Font font = t.getFont();
					float size = font.getSize() + 2.0f;
					t.setFont( font.deriveFont(size) );

	    		}

	    		//For decrementing the font size of text
	    		else if(e.getActionCommand()=="Dec")
	    		{
	    			Font font = t.getFont();
					float size = font.getSize() - 2.0f;
					t.setFont( font.deriveFont(size) );

	    		}

	    		//Deleting a selected content
	    		else if(e.getActionCommand()=="Delete")
	    		{
				
	    			sec_storage= (String)t.getSelectedText();
	    			t.setText(t.getText().replace(t.getSelectedText(),""));
					String cmd="Delete";
	    			stack.push((String)cmd);
					pos=t.getCaretPosition();
	    			
	    		}

	    		//For undo operation (only for delete)
	    		else if(e.getActionCommand()=="Undo")
	    		{
	    			
					String s=(String)stack.pop(); System.out.println(s);
	    			if(s=="Delete")
	    			{
	    				t.insert(sec_storage, pos);
	    			}
	    		}
				
				//For finding a particular content
				else if(e.getActionCommand()=="Find")
	    		{
	    			//Designing a new small frame for find window
					f= new JFrame();
					d= new JDialog(f,"Find", true);
					d.setSize(300,180);
					d.setLayout(new FlowLayout());
					what_to_find= new JLabel("Find What");
					ip= new JTextField();
					ip.setPreferredSize( new Dimension( 200, 24 ) );
					find= new JButton("Find in text");
					cancel= new JButton("Cancel");
					revert= new JButton("Revert");
					d.add(what_to_find); 
					d.add(ip);

					//Event handling for find
					find.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								try{
										
										HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);
             							Highlighter highlighter = t.getHighlighter();
										String original_text= t.getText();
										String to_be_found= ip.getText()+"*";
										//Use regex for this
										Pattern pattern = Pattern.compile(to_be_found);
										Matcher m = pattern.matcher(original_text);	    								
	    								while(m.find())
	    								{

	    									int p0 = m.start();
      										int p1 = m.end();
      										System.out.println(p0);
      										System.out.println(p1);
      										highlighter.addHighlight(p0, p1, painter);
      										
      			 
      									}
      								}
      			catch(Exception evt)
      			{
      				System.out.println(evt.getMessage());
      			}
							}

						});
					d.add(find);

					//Event handling for cancel
					cancel.addActionListener(new ActionListener()
					{


						public void actionPerformed(ActionEvent e)
						{
							try
							{
										HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.white);
             							Highlighter highlighter = t.getHighlighter();
										String original_text= t.getText();
										String to_be_found= ip.getText()+"*";
										//Use regex for this
										Pattern pattern = Pattern.compile(to_be_found);
										Matcher m = pattern.matcher(original_text);	    								
	    								while(m.find())
	    								{

	    									int p0 = m.start();
      										int p1 = m.end();
      										System.out.println(p0);
      										System.out.println(p1);
      										highlighter.addHighlight(p0, p1, painter);
      										
      			 
      									}
							
							
						}
						catch(Exception e1)
						{
							System.out.println(e1.getMessage());
						}
						d.setVisible(false);
						}

					});
					d.add(cancel);

					d.setVisible(true);

					//Event handling for revert
					/*revert.addActionListener(new ActionListener()
						{

						});*/


	    		}

	    		//Replacing operation
	    		else if(e.getActionCommand()=="Replace")
	    		{
	    			System.out.println("i am Sujata");
	    			JLabel find_what= new JLabel("Find What");
	    			JLabel rep_with= new JLabel("Replace with");
	    			f1= new JFrame();
					d1= new JDialog(f1,"Replace", true);
					d1.setSize(300,180);
					d1.setLayout(new GridLayout(3,2));
					replace_what= new JTextField();
					replace_what.setPreferredSize( new Dimension( 200, 24 ) );
					replacable= new JTextField();
					replacable.setPreferredSize( new Dimension( 200, 24 ) );
					replace= new JButton("Replace in text");
					cancel= new JButton("Cancel");
					
					d1.add(find_what);
					d1.add(replace_what);
					d1.add(rep_with); 
					d1.add(replacable);

					//Event handling for replacing
					replace.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							String original_text= t.getText();
							String source=replace_what.getText();
							String destination= replacable.getText();
							original_text= original_text.replaceAll(source,destination);
							System.out.println(original_text);
							t.setText("");
							t.setText(original_text);

						}
					});

					d1.add(replace);
					cancel.addActionListener(new ActionListener()
					{

						public void actionPerformed(ActionEvent e)
						{
							d1.setVisible(false);
						}

					});
					d1.add(cancel);

					d1.setVisible(true);



	    			
	    		}

	    		//Emojis' Event handling
	    		else if(e.getActionCommand()=="\u263a")
	    		{
	    			t.append("\u263a");
	    		}
	    		else if(e.getActionCommand()=="\uD83D\uDE02")
	    		{
	    			t.append("\uD83D\uDE02");
	    		}
	    		else if(e.getActionCommand()=="\uD83D\uDE2D")
	    		{
	    			t.append("\uD83D\uDE2D");
	    		}
	    		else if(e.getActionCommand()=="\uD83D\uDE21")
	    		{
	    			t.append("\uD83D\uDE21");
	    		}
	    		


	   } 

	   }


	   //Int the main function of Demo class we r creating an object of Demo class and thus calling the default constructor overridden by us.
		public static void main(String args[])
		{
			Notepad d= new Notepad();
		}

		//Thankyou for reading my code.

	}