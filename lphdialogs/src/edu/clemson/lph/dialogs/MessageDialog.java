package edu.clemson.lph.dialogs;
/*
Copyright 2014 Michael K Martin

This file is part of Civet.

Civet is free software: you can redistribute it and/or modify
it under the terms of the Lesser GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Civet is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the Lesser GNU General Public License
along with Civet.  If not, see <http://www.gnu.org/licenses/>.
*/
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;

public class MessageDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private String mTitle;
	private String mMessage;
	private int deltaX = 0;
	private int deltaY = 0;
	private boolean bOK = false;
	public static final int OK_ONLY = 0;
	public static final int CANCEL_ONLY = 1;
	public static final int BOTH_BUTTONS = 2;
	private int iButtons = BOTH_BUTTONS;
	private Window parent = null;


	/**
	 * Create the dialog.
	 */
	public MessageDialog() {
	}

	public MessageDialog( String sTitle, String sMessage ) {
		mTitle = sTitle;
		mMessage = sMessage;
	}

	public MessageDialog( Window parent, String sTitle, String sMessage ) {
		super( parent );
		this.parent = parent;
		mTitle = sTitle;
		mMessage = sMessage;
	}
	
	@Override
	public void setVisible( boolean bShow ) {
		if( bShow ) {
			initGui();
		}
		super.setVisible( bShow );
	}
	
	@Override
	public void setModal( boolean bModal ) {
		super.setModal(bModal);
	}

	public void setDeltas( int deltaX, int deltaY ) {
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}

	public void setButtons( int iButtons ) {
		this.iButtons = iButtons;
	}

	public boolean isExitOK() {
		return bOK;
	}

	public void center() {
		//Center the window
		boolean bSmall = false;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		if( frameSize.height > screenSize.height ) {
			frameSize.height = screenSize.height;
			bSmall = true;
		}
		if( frameSize.width > screenSize.width ) {
			frameSize.width = screenSize.width;
			bSmall = true;
		}
		if( bSmall ) {
			setLocation( (screenSize.width - frameSize.width) / 2, 0);
		}
		else {
			setLocation( deltaX + (screenSize.width - frameSize.width) / 2,
					deltaY + (screenSize.height - frameSize.height) / 2);
		}
	}
	
	public static void main( String args[]) {
		MessageDialog.showMessage(null, "Test Message", "<Veterinarian LicenseNumber=6774 NationalAccreditationNumber=013531>\n<Person>\n<Name>ASHLEY ARMSTRONG, DVM</Name>\n<Phone Number=8122779500 Type=Unknown/>\n</Person>\n<Address>\n<Line1>1652 SPRINGVILLE-JUDAH RD</Line1>\n<Town>SPRINGVILLE</Town>\n<State>IN</State>\n<ZIP>47462</ZIP>\n<Country>USA</Country>\n</Address>\n</Veterinarian>\n<MovementPurposes>\n<MovementPurpose>pet</MovementPurpose>\n</MovementPurposes>\n<Origin>\n<PremName/>\n<Address>\n<Line1>591 DONICA CHURCH RD</Line1>\n<Town>BEDFORD</Town>\n<State>IN</State>\n<ZIP>47421</ZIP>\n<Country>USA</Country>\n</Address>\n<Person>\n<Name>NOBLES-FRALEY, PAULA</Name>\n<Phone Number=3523272877 Type=Unknown/>\n</Person>");
	}

	private void initGui() {
		setBounds(100, 100, 450, 300);
		setTitle( mTitle );
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout());
		JScrollPane jspText = new JScrollPane();
		contentPanel.add(jspText, BorderLayout.CENTER);
		{
			JTextArea textArea = new JTextArea();
			textArea.setEditable(false);
			textArea.setWrapStyleWord(true);
			textArea.setLineWrap(true);
			textArea.setText( mMessage );
			jspText.setViewportView(textArea);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			if( iButtons == OK_ONLY || iButtons == BOTH_BUTTONS )
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						bOK = true;
						MessageDialog.this.setVisible(false);
						if( parent != null )
							parent.requestFocus();
					}
				});
			}
			if( iButtons == CANCEL_ONLY || iButtons == BOTH_BUTTONS )
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						bOK = false;
						MessageDialog.this.setVisible(false);
						if( parent != null )
							parent.requestFocus();
					}
				});
			}
		}
		center();
	}

	public static void showMessage( Window parent, String sTitle, String sMessage ) {
		showMessage( parent, sTitle, sMessage, BOTH_BUTTONS );
	}	

	public static void showMessage( Window parent, String sTitle, String sMessage, int iButtons ) {
		MessageDialog me = new MessageDialog( parent, sTitle, sMessage );
		me.setButtons(iButtons);
		me.setModal(true);
		me.setVisible(true);
		me.requestFocus();
	}

	public static void messageLater( Window parent, String sTitle, String sMessage ) {
		messageLater( parent, sTitle, sMessage, BOTH_BUTTONS );
	}	

	public static void messageLater( Window parent, String sTitle, String sMessage, int iButtons ) {
		final Window fParent = parent;
		final String fTitle = sTitle;
		final String fMessage = sMessage;
		final int fButtons = iButtons;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MessageDialog me = new MessageDialog( fParent, fTitle, fMessage );
				me.setButtons(fButtons);
				me.setModal(true);
				me.setVisible(true);
				me.requestFocus();
			}
		});
	}

	public static void messageWait( Window parent, String sTitle, String sMessage ) {
		messageWait( parent, sTitle, sMessage, BOTH_BUTTONS );
	}	

	public static void messageWait( Window parent, String sTitle, String sMessage, int iButtons ) {
		final Window fParent = parent;
		final String fTitle = sTitle;
		final String fMessage = sMessage;
		final int fButtons = iButtons;
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					MessageDialog msg = new MessageDialog( fParent, fTitle, fMessage );
					msg.setButtons(fButtons);
					msg.setModal(true);
					msg.setVisible(true);
					msg.requestFocus();
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}


}// End class MessageDialog
