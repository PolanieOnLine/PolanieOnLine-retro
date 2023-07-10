/* $Id: j2DClient.java,v 1.427 2012/12/01 08:09:02 kiheru Exp $ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.GameLoop;
import games.stendhal.client.GameObjects;
import games.stendhal.client.GameScreen;
import games.stendhal.client.PerceptionListenerImpl;
import games.stendhal.client.StaticGameLayers;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.UserContext;
import games.stendhal.client.World;
import games.stendhal.client.WorldObjects;
import games.stendhal.client.stendhal;
import games.stendhal.client.actions.SlashActionRepository;
import games.stendhal.client.entity.User;
import games.stendhal.client.entity.factory.EntityMap;
import games.stendhal.client.gui.buddies.BuddyPanelController;
import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.client.gui.chattext.ChatCompletionHelper;
import games.stendhal.client.gui.chattext.ChatTextController;
import games.stendhal.client.gui.group.GroupPanelController;
import games.stendhal.client.gui.layout.FreePlacementLayoutManager;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.gui.map.MapPanelController;
import games.stendhal.client.gui.spells.Spells;
import games.stendhal.client.gui.stats.StatsPanelController;
import games.stendhal.client.gui.styled.StyledTabbedPaneUI;
import games.stendhal.client.gui.wt.core.SettingChangeAdapter;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.listener.PositionChangeMulticaster;
import games.stendhal.client.sound.facade.SoundFileType;
import games.stendhal.client.sound.facade.SoundGroup;
import games.stendhal.client.sound.facade.SoundSystemFacade;
import games.stendhal.client.sound.nosound.NoSoundFacade;
import games.stendhal.client.sprite.DataLoader;
import games.stendhal.common.CollisionDetection;
import games.stendhal.common.Debug;
import games.stendhal.common.NotificationType;
import games.stendhal.common.constants.SoundLayer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.TabbedPaneUI;

import marauroa.client.net.IPerceptionListener;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/** The main class that create the screen and starts the arianne client. */
public class j2DClient implements UserInterface {
	static {
		// This is potentially the first loaded GUI component (happens when
		// using web start)
		Initializer.init();
	}
	
	/** Scrolling speed when using the mouse wheel. */
	private static final int SCROLLING_SPEED = 8;
	/** Background color of the private chat tab. Light blue. */
	private static final String PRIVATE_TAB_COLOR = "0x3c1e00";
	/** Property name used to determine if scaling is wanted. */
	private static final String SCALE_PREFERENCE_PROPERTY = "ui.scale_screen";
	final static boolean shouldFill = true;

	/**
	 * A shared [singleton] copy.
	 */
	private static j2DClient sharedUI;

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(j2DClient.class);

	private MainFrame mainFrame;
	private QuitDialog quitDialog;

	private GameScreen screen;
	private final ScreenController screenController;

	private JLayeredPane pane;

	/** Chat channels */
	private NotificationChannelManager channelManager;

	private ContainerPanel containerPanel;

	private boolean gameRunning;


	ChatTextController chatText = new ChatTextController();

	/** settings panel. */
	private SettingsPanel settings;

	/** the Character panel. */
	private Character character;

	/** the Key ring panel. */
	private KeyRing keyring;

	/** the minimap panel. */
	private MapPanelController minimap;

	/** the inventory.*/
	private SlotWindow inventory;

	private Spells spells;

	private User lastuser;

	private boolean offline;

	OutfitDialog outfitDialog;


	private final PositionChangeMulticaster positionChangeListener = new PositionChangeMulticaster();

	private UserContext userContext;

	/** Key handling */
	private GameKeyHandler gameKeyHandler;


	/**
	 * Get the default UI.
	 * @return  the instance
	 */
	public static j2DClient get() {
		return sharedUI;
	}

	/**
	 * Set the shared [singleton] value.
	 *
	 * @param sharedUI
	 *            The Stendhal UI.
	 */
	public static void setDefault(final j2DClient sharedUI) {
		j2DClient.sharedUI = sharedUI;
		ClientSingletonRepository.setUserInterface(sharedUI);
	}


	private final IPerceptionListener perceptionListener = new PerceptionListenerImpl() {
		int times;
		@Override
		public void onSynced() {
			setOffline(false);
			times = 0;
			logger.debug("Synced with server state.");
			addEventLine(new HeaderLessEventLine("Zsynchronizowany",
					NotificationType.CLIENT));
		}

		@Override
		public void onUnsynced() {
			times++;

			if (times > 3) {
				logger.debug("Request resync");
				addEventLine(new HeaderLessEventLine("Niezsynchronizowany: Synchronizuję...",
						NotificationType.CLIENT));
			}

		}
	};

	/**
	 * The stendhal client.
	 */
	private StendhalClient client;

	private SoundSystemFacade soundSystemFacade;


	/**
	 * A constructor for JUnit tests.
	 */
	public j2DClient() {
		setDefault(this);
		screenController = null;
	}

	/**
	 * Create new j2DClient.
	 *
	 * @param client
	 * @param userContext
	 */
	public j2DClient(final StendhalClient client, final UserContext userContext) {
		this.client = client;
		this.userContext = userContext;
		setDefault(this);

		final Dimension screenSize = stendhal.getScreenSize();

		/*
		 * Add a layered pane for the game area, so that we can have
		 * windows on top of it
		 */
		pane = new JLayeredPane();
		pane.setLayout(new FreePlacementLayoutManager());

		/*
		 * Create the main game screen
		 */
		screen = new GameScreen(client);
		screenController = new ScreenController(screen);
		GameScreen.setDefaultScreen(screen);

		// ... and put it on the ground layer of the pane
		pane.add(screen, Component.LEFT_ALIGNMENT, JLayeredPane.DEFAULT_LAYER);

		client.addZoneChangeListener(screen);
		positionChangeListener.add(screenController);

		/*
		 * Register the slash actions in the client side command line parser.
		 * This needs to be at least before getting the actions to
		 * ChatCompletionHelper.
		 */
		SlashActionRepository.register();
		
		final KeyListener tabcompletion = new ChatCompletionHelper(chatText, World.get().getPlayerList().getNamesList(),
				SlashActionRepository.getCommandNames());
		chatText.addKeyListener(tabcompletion);

		/*
		 * Always redirect focus to chat field
		 */
		screen.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent e) {
				chatText.getPlayerChatText().requestFocus();
			}
		});

		// On Screen windows
		/*
		 * Quit dialog
		 */
		quitDialog = new QuitDialog();
		pane.add(quitDialog.getQuitDialog(), JLayeredPane.MODAL_LAYER);

		/*
		 * Game log
		 */
		final JComponent chatLogArea = createLogArea();
		chatLogArea.setPreferredSize(new Dimension(getWidth(), 171));

		// *** Key handling ***
		gameKeyHandler = new GameKeyHandler(client, screen);
		// add a key input system (defined below) to our canvas so we can
		// respond to key pressed
		chatText.addKeyListener(gameKeyHandler);
		screen.addKeyListener(gameKeyHandler);
		// Also redirect key presses to the chatlog tabs, so that the tabs
		// can be switched with ctrl-PgUp/Down
		chatText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				/*
				 * Redispatch only if CTRL is pressed. Otherwise any arrow key
				 * press will be interpreted as switching log tabs.
				 *
				 * What should be used for Macs?
				 */
				if (e.isControlDown()) {
					chatLogArea.dispatchEvent(e);
					// The log tab contents like stealing the focus if they get
					// key events.
					chatText.getPlayerChatText().requestFocus();
				}
			}
		});

		// Display a warning message in case the screen size was adjusted
		// This is a temporary solution until this issue is fixed server side.
		// I hope that it discourages its use without the risks of unupdateable
		// clients being distributed
		if (!screenSize.equals(new Dimension(800, 576))) {
			addEventLine(new HeaderLessEventLine("Używasz niestandardowego rozmiaru obrazu: " + getWidth() + "x" + getHeight(), NotificationType.NEGATIVE));
		}

		// Display a hint if this is a debug client
		if (Debug.PRE_RELEASE_VERSION != null) {
			addEventLine(new HeaderLessEventLine("This is a pre release test client: " + Debug.VERSION + " - " + Debug.PRE_RELEASE_VERSION, NotificationType.CLIENT));
		}

		// set some default window positions
		final WtWindowManager windowManager = WtWindowManager.getInstance();
		windowManager.setDefaultProperties("corpse", false, 0, 190);
		windowManager.setDefaultProperties("chest", false, 100, 190);

		/*
		 * Finally create the window, and place all the components in it
		 */
		// Create the main window
		mainFrame = new MainFrame();
		JComponent glassPane = DragLayer.get();
		mainFrame.getMainFrame().setGlassPane(glassPane);
		glassPane.setVisible(true);

		// *** Create the layout ***
		// left side panel
		final JComponent leftColumn = createLeftPanel();

		// Chat entry and chat log. The chatlogs are in tabs so they need a
		// patterned background.
		final JComponent chatBox = new JPanel();
		chatBox.setBorder(null);
		chatBox.setLayout(new SBoxLayout(SBoxLayout.VERTICAL));
		// Set maximum size to prevent the entry requesting massive widths, but
		// force expand if there's extra space anyway
		chatText.getPlayerChatText().setMaximumSize(new Dimension(screenSize.width, Integer.MAX_VALUE));
		chatBox.add(chatText.getPlayerChatText(), SBoxLayout.constraint(SLayout.EXPAND_X));

		chatBox.add(chatLogArea, SBoxLayout.constraint(SLayout.EXPAND_X, SLayout.EXPAND_Y));

		// Give the user the ability to make the the game area less tall
		final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pane, chatBox);
		splitPane.setBorder(null);
		// Works for showing the resize, but is extremely flickery
		//splitPane.setContinuousLayout(true);
		// Moving either divider will result in the screen resized. Pass it to
		// the game screen so that it can recenter the player.
		pane.addComponentListener(new SplitPaneResizeListener(screen));

		containerPanel = createContainerPanel();

		// Avoid panel drawing overhead
		final Container windowContent = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL);
		mainFrame.getMainFrame().setContentPane(windowContent);

		// Finally add the left pane, and the games screen + chat combo
		// Make the panel take any horizontal resize
		leftColumn.setMinimumSize(new Dimension());
		/*
		 * Fix the container panel size, so that it is always visible
		 */
		containerPanel.setMinimumSize(containerPanel.getPreferredSize());
		containerPanel.setMaximumSize(containerPanel.getPreferredSize());
		
		// Splitter between the left column and game screen
		final JSplitPane horizSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftColumn, splitPane);

		// Ensure that the limits are obeyed even when the component is resized
		horizSplit.addComponentListener(new ComponentAdapter() {
			// Start with a large value, so that the divider is placed as left
			// as possible
			int oldWidth = Integer.MAX_VALUE;
			
			@Override
			public void componentResized(ComponentEvent e) {
				if (screen.isScaled()) {
					/*
					 * Default behavior is otherwise reasonable, except the
					 * user will likely want to use the vertical space for the
					 * game screen.
					 * 
					 * Try to keep the aspect ratio near the optimum; the sizes
					 * have not changed when this gets called, so push it to the
					 * EDT.
					 */
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							double hScale = screen.getWidth() / (double) screenSize.width;
							int preferredLocation = (int) (hScale * screenSize.height);
							splitPane.setDividerLocation(preferredLocation);
						}
					});
				} else {
					int position = horizSplit.getDividerLocation();
					/*
					 * The trouble: the size of the game screen is likely the one
					 * that the player wants to preserve when making the window
					 * smaller. Swing provides no default way to the old component
					 * size, so we stash the interesting dimension in oldWidth. 
					 */
					int width = horizSplit.getWidth();
					int oldRightDiff = oldWidth - position;
					int widthChange = width - oldWidth;
					int underflow = widthChange + position;
					if (underflow < 0) {
						/*
						 * Extreme size reduction. The divider location would have
						 * changed as the result. Use the previous location instead
						 * of the current.
						 */
						oldRightDiff = oldWidth - horizSplit.getLastDividerLocation();
					}
					position = width - oldRightDiff;

					position = Math.min(position, horizSplit.getMaximumDividerLocation());
					position = Math.max(position, horizSplit.getMinimumDividerLocation());

					horizSplit.setDividerLocation(position);
					oldWidth = horizSplit.getWidth();
				}
			}
		});
		/** Used as a workaround for BasicSplitPaneUI bugs */
		final int divWidth = splitPane.getDividerSize();
		
		pane.setPreferredSize(new Dimension(screenSize.width + divWidth, screenSize.height));
		horizSplit.setBorder(null);
		
		windowContent.add(horizSplit, SBoxLayout.constraint(SLayout.EXPAND_Y, SLayout.EXPAND_X));
		windowContent.add(containerPanel, SBoxLayout.constraint(SLayout.EXPAND_Y));

		WtWindowManager.getInstance().registerSettingChangeListener(SCALE_PREFERENCE_PROPERTY,
				new SettingChangeAdapter(SCALE_PREFERENCE_PROPERTY, "true") {
			@Override
			public void changed(String newValue) {
				boolean scale = Boolean.parseBoolean(newValue);
				screen.setUseScaling(scale);
				if (scale) {
					// Clear the resize limits
					splitPane.setMaximumSize(null);
					pane.setMaximumSize(null);
				} else {
					// Set the limits
					splitPane.setMaximumSize(new Dimension(screenSize.width + divWidth, Integer.MAX_VALUE));
					pane.setMaximumSize(screenSize);
					// The user may have resized the screen outside allowed
					// parameters
					int overflow = horizSplit.getWidth() - horizSplit.getDividerLocation() - screenSize.width - divWidth;
					if (overflow > 0) {
						horizSplit.setDividerLocation(horizSplit.getDividerLocation() + overflow);
					}
					if (splitPane.getDividerLocation() > screenSize.height) {
						splitPane.setDividerLocation(screenSize.height);
					}
				}
			}
		});
		
		/*
		 * Handle focus assertion and window closing
		 */
		mainFrame.getMainFrame().addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(final WindowEvent ev) {
				chatText.getPlayerChatText().requestFocus();
			}

			@Override
			public void windowActivated(final WindowEvent ev) {
				chatText.getPlayerChatText().requestFocus();
			}

			@Override
			public void windowGainedFocus(final WindowEvent ev) {
				chatText.getPlayerChatText().requestFocus();
			}

			@Override
			public void windowClosing(final WindowEvent e) {
				requestQuit();
			}
		});

		mainFrame.getMainFrame().pack();
		horizSplit.setDividerLocation(leftColumn.getPreferredSize().width);
		setInitialWindowStates();

		/*
		 *  A bit roundabout way to calculate the desired minsize, but
		 *  different java versions seem to take the window decorations
		 *  in account in rather random ways.
		 */
		final int width = mainFrame.getMainFrame().getWidth()
		- minimap.getComponent().getWidth() - containerPanel.getWidth();
		final int height = mainFrame.getMainFrame().getHeight() - chatLogArea.getHeight();

		mainFrame.getMainFrame().setMinimumSize(new Dimension(width, height));
		mainFrame.getMainFrame().setVisible(true);

		/*
		 * For small screens. Setting the maximum window size does
		 * not help - pack() happily ignores it.
		 */
		Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		Dimension current = mainFrame.getMainFrame().getSize();
		mainFrame.getMainFrame().setSize(Math.min(current.width, maxBounds.width),
				Math.min(current.height, maxBounds.height));

		/*
		 * Needed for small screens; Sometimes the divider is placed
		 * incorrectly unless we explicitly set it. Try to fit it on the
		 * screen and show a bit of the chat.
		 */
		splitPane.setDividerLocation(Math.min(screenSize.height,
				maxBounds.height  - 80));

		checkAndComplainAboutJavaImplementation();
		WorldObjects.addWorldListener(getSoundSystemFacade());
		WindowUtils.watchFontSize(mainFrame.getMainFrame());
	} // constructor

	/**
	 * Create the left side panel of the client.
	 *
	 * @return A component containing the components left of the game screen
	 */
	private JComponent createLeftPanel() {
		minimap = new MapPanelController(client);
		positionChangeListener.add(minimap);
		final StatsPanelController stats = StatsPanelController.get();
		final BuddyPanelController buddies = BuddyPanelController.get();
		ScrolledViewport buddyScroll = new ScrolledViewport((JComponent) buddies.getComponent());
		buddyScroll.setScrollingSpeed(SCROLLING_SPEED);
		final JComponent buddyPane = buddyScroll.getComponent();
		buddyPane.setBorder(null);

		final JComponent leftColumn = SBoxLayout.createContainer(SBoxLayout.VERTICAL);
		leftColumn.add(minimap.getComponent(), SBoxLayout.constraint(SLayout.EXPAND_X));
		leftColumn.add(stats.getComponent(), SBoxLayout.constraint(SLayout.EXPAND_X));

		// Add a background for the tabs. The column itself has none.
		JPanel tabBackground = new JPanel();
		tabBackground.setBorder(null);
		tabBackground.setLayout(new SBoxLayout(SBoxLayout.VERTICAL));
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.BOTTOM);
		// Adjust the Tab Width, if we can. The default is pretty if there's
		// space, but in the column there are no pixels to waste.
		TabbedPaneUI ui = tabs.getUI();
		if (ui instanceof StyledTabbedPaneUI) {
			((StyledTabbedPaneUI) ui).setTabLabelMargins(1);
		}
		tabs.setFocusable(false);
		tabs.add("Przyjaciele", buddyPane);
		
		tabs.add("Grupa", GroupPanelController.get().getComponent());
		
		tabBackground.add(tabs, SBoxLayout.constraint(SLayout.EXPAND_X, SLayout.EXPAND_Y));
		leftColumn.add(tabBackground, SBoxLayout.constraint(SLayout.EXPAND_X, SLayout.EXPAND_Y));

		return leftColumn;
	}

	/**
	 * Create the container panel (right side panel), and its child components.
	 *
	 * @return container panel
	 */
	private ContainerPanel createContainerPanel() {
		ContainerPanel containerPanel = new ContainerPanel();
		containerPanel.setMinimumSize(new Dimension(0, 0));
		JPanel pane = new JPanel(new GridBagLayout());
		containerPanel.addRepaintable(pane);
		GridBagConstraints c = new GridBagConstraints();
		if (shouldFill) {
			//natural height, maximum width
			c.fill = GridBagConstraints.HORIZONTAL;
		}
		
		/*
		 * Contents of the containerPanel
		 */
		// The setting bar to the top
		settings = new SettingsPanel();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		pane.add(settings, c);
		
		// Character window
		character = new Character();
		character.setAlignmentX(Component.LEFT_ALIGNMENT);
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		pane.add(character, c);
		
		keyring = new KeyRing();
		// keyring's types are more limited, but it's simpler to let the server
		// handle those
		keyring.setAcceptedTypes(EntityMap.getClass("item", null, null));
		keyring.setAlignmentX(Component.LEFT_ALIGNMENT);
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		pane.add(keyring, c);
		client.addFeatureChangeListener(keyring);
		
		// Create the bag window
		inventory = new SlotWindow("Plecak", 6, 6);
		inventory.setAcceptedTypes(EntityMap.getClass("item", null, null));
		inventory.setCloseable(false);
		inventory.setInspector(containerPanel);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		pane.add(inventory, c);
		
		spells = new Spells();
		spells.setAcceptedTypes(EntityMap.getClass("spell", null, null));
		spells.setAlignmentX(Component.LEFT_ALIGNMENT);
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		pane.add(spells, c);
		client.addFeatureChangeListener(spells);

		return containerPanel;
	}

	/**
	 * Modify the states of the on screen windows. The window manager normally
	 * restores the state of the window as it was on the previous session. For
	 * some windows this is not desirable.
	 * <p>
	 * <em>Note:</em> This need to be called from the event dispatch thread.
	 */
	private void setInitialWindowStates() {
		/*
		 * Window manager may try to restore the visibility of the dialog when
		 * it's added to the pane.
		 */
		quitDialog.getQuitDialog().setVisible(false);
		// Windows may have been closed in old clients
		character.setVisible(true);
		inventory.setVisible(true);
		/*
		 * Keyring, on the other hand, *should* be hidden until revealed
		 * by feature change
		 */
		keyring.setVisible(false);

		// spells should also be invisible until revealed by a feature change
		spells.setVisible(false);
	}

	private void checkAndComplainAboutJavaImplementation() {
		final String vmName = System.getProperty("java.vm.name", "unknown").toLowerCase(Locale.ENGLISH);
		if ((vmName.indexOf("hotspot") < 0) && (vmName.indexOf("openjdk") < 0)) {
			final String text = "PolskaOnLine jest przeznaczona i testowana dla Oracle Java i OpenJDK. Używasz "
				+ System.getProperty("java.vm.vendor", "unknown") + " " 
				+ System.getProperty("java.vm.name", "unknown") 
				+ ". Mogą wyniknąć problemy takie jak czarny lub szary ekran.\n"
				+ " Jeżeli masz doświadczenie w programowaniu z JDK to zapraszamy do współpracy.";
			addEventLine(new HeaderLessEventLine(text, NotificationType.ERROR));
		}
	}

	private void cleanup() {
		chatText.saveCache();
		getSoundSystemFacade().exit();
		logger.debug("Exit");
		System.exit(0);
	}

	/**
	 * Add a native in-window dialog to the screen.
	 *
	 * @param comp
	 *            The component to add.
	 */
	public void addDialog(final Component comp) {
		pane.add(comp, JLayeredPane.PALETTE_LAYER);
	}

	/** Time of receiving the last network activity */
	long lastMessageHandle;

	/**
	 * Start the game loop thread.
	 */
	public void startGameLoop() {
		try {
			SoundGroup group = initSoundSystem();
			group.play("harp-1", 0, null, null, false, true);
		} catch (RuntimeException e) {
			logger.error(e, e);
		}

		GameLoop loop = GameLoop.get();
		final GameObjects gameObjects = client.getGameObjects();
		final StaticGameLayers gameLayers = client.getStaticGameLayers();

		loop.runAllways(new GameLoop.PersistentTask() {
			@Override
			public void run(long time, int delta) {
				gameLoop(time, delta, gameLayers, gameObjects);
			}
		});

		loop.runAtQuit(new Runnable() {
			@Override
			public void run() {
				cleanup();
			}
		});

		lastMessageHandle = System.currentTimeMillis();
		gameRunning = true;

		loop.start();
	}

	/**
	 * Main game loop contents. Updates objects, and requests redraws.
	 *
	 * @param time current time
	 * @param delta difference to previous calling time
	 * @param gameLayers
	 * @param gameObjects
	 */
	private void gameLoop(final long time, final int delta,
			final StaticGameLayers gameLayers, final GameObjects gameObjects) {
		// Check logouts first, in case something goes wrong with the drawing
		// code

		if (!gameRunning) {
			logger.info("Request logout");
			try {
				/*
				 * We request server permision to logout. Server can deny
				 * it, unless we are already offline.
				 */
				if (offline || client.logout()) {
					GameLoop.get().stop();
				} else {
					logger.warn("You can't logout now.");
					gameRunning = true;
				}
			} catch (final Exception e) { // catch InvalidVersionException, TimeoutException and BannedAddressException
				/*
				 * If we get a timeout exception we accept exit request.
				 */
				logger.error(e, e);
				GameLoop.get().stop();
			}
		}

		// Shows a offline icon if the connection is broken
		setOffline(!client.getConnectionState());

		// figure out what time it is right after the screen flip then
		// later we can figure out how long we have been doing redrawing
		// / networking, then we know how long we need to sleep to make
		// the next flip happen at the right time
		screenController.nextFrame();

		logger.debug("Move objects");
		gameObjects.update(delta);

		if (gameLayers.isAreaChanged()) {
			// Same thread as the ClientFramework loop, so these should
			// be save
			/*
			 * Update the screen
			 */
			screenController.setWorldSize(gameLayers.getWidth(), gameLayers.getHeight());

			// [Re]create the map.

			final CollisionDetection cd = gameLayers.getCollisionDetection();
			final CollisionDetection pd = gameLayers.getProtectionDetection();
			final CollisionDetection sd = gameLayers.getSecretDetection();

			if (cd != null) {
				minimap.update(cd, pd, sd, gameLayers.getAreaName(), gameLayers.getDangerLevel());
			}
			gameLayers.resetChangedArea();
		}

		final User user = User.get();

		if (user != null) {
			// check if the player object has changed.
			// Note: this is an exact object reference check
			if (user != lastuser) {
				character.setPlayer(user);
				keyring.setSlot(user, "keyring");
				spells.setSlot(user, "spells");
				inventory.setSlot(user, "bag");
				lastuser = user;
			}
		}

		triggerPainting();

		logger.debug("Query network");

		if (client.loop(0)) {
			lastMessageHandle = time;
		}

		gameKeyHandler.processDelayedDirectionRelease();
	}

	private int paintCounter;
	private void triggerPainting() {
		if (mainFrame.getMainFrame().getState() != Frame.ICONIFIED) {
			paintCounter++;
			if (mainFrame.getMainFrame().isActive() || System.getProperty("stendhal.skip.inactive", "false").equals("false") || paintCounter >= 20) {
				paintCounter = 0;
				logger.debug("Draw screen");
				minimap.refresh();
				containerPanel.repaintChildren();
				screen.repaint();
			}
		}
    }

	private SoundGroup initSoundSystem() {
		SoundGroup group = getSoundSystemFacade().getGroup(SoundLayer.USER_INTERFACE.groupName);
		group.loadSound("harp-1", "harp-1.ogg", SoundFileType.OGG, false);
		group.loadSound("click-4", "click-4.ogg", SoundFileType.OGG, false);
		group.loadSound("click-5", "click-5.ogg", SoundFileType.OGG, false);
		group.loadSound("click-6", "click-6.ogg", SoundFileType.OGG, false);
		group.loadSound("click-8", "click-8.ogg", SoundFileType.OGG, false);
		group.loadSound("click-10", "click-10.ogg", SoundFileType.OGG, false);
		return group;
	}

	/**
	 * Shutdown the client. Save state and tell the main loop to stop.
	 */
	public void shutdown() {
		gameRunning = false;

		// try to save the window configuration
		WtWindowManager.getInstance().save();
	}

	//
	// <StendhalGUI>
	//

	/**
	 * Add a new window.
	 *
	 * @param mw
	 *            A managed window.
	 *
	 * @throws IllegalArgumentException
	 *             If an unsupported ManagedWindow is given.
	 */
	public void addWindow(final ManagedWindow mw) {
		if (mw instanceof InternalManagedWindow) {
			addDialog((InternalManagedWindow) mw);
		} else {
			throw new IllegalArgumentException("Unsupport ManagedWindow type: "
					+ mw.getClass().getName());
		}
	}

	//
	// j2DClient
	//

	/**
	 * Add an event line.
	 *
	 */
	@Override
	public void addEventLine(final EventLine line) {
		channelManager.addEventLine(line);
	}

	/**
	 * adds a text box on the screen
	 *
	 * @param x  x
	 * @param y  y
	 * @param text text to display
	 * @param type type of text
	 * @param isTalking chat?
	 */
	@Override
	public void addGameScreenText(final double x, final double y, final String text, final NotificationType type,
			final boolean isTalking) {
		screenController.addText(x, y, text, type, isTalking);
	}

	/**
	 * Display a box for a reached achievement
	 *
	 * @param title the title of the achievement
	 * @param description the description of the achievement
	 * @param category the category of the achievement
	 */
	@Override
	public void addAchievementBox(String title, String description, String category) {
		screen.addAchievementBox(title, description, category);
	}

	/**
	 * Initiate outfit selection by the user.
	 */
	public void chooseOutfit() {
		int outfit;
		final RPObject player = userContext.getPlayer();

		if (player.has("outfit_org")) {
			outfit = player.getInt("outfit_org");
		} else {
			outfit = player.getInt("outfit");
		}

		if (outfitDialog == null) {
			// Here we actually want to call new OutfitColor(). Modifying
			// OutfitColor.PLAIN would be a bad thing.
			outfitDialog = new OutfitDialog(mainFrame.getMainFrame(),
					"Ustaw wygląd", outfit, new OutfitColor(player));
			outfitDialog.setVisible(true);
		} else {
			outfitDialog.setState(outfit, OutfitColor.get(player));
			outfitDialog.setVisible(true);
			outfitDialog.toFront();
		}
	}

	/**
	 * Create the chat log tabs.
	 *
	 * @return chat log area
	 */
	private JComponent createLogArea() {
		final JTabbedPane tabs = new JTabbedPane(JTabbedPane.BOTTOM);
		tabs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int i = tabs.getSelectedIndex();
				NotificationChannel channel = channelManager.getChannels().get(i);
				channelManager.setVisibleChannel(channel);
				// Remove modified marker
				String channelName = tabs.getTitleAt(i);
				if (channelName.startsWith("* ")) {
					tabs.setTitleAt(i, channelName.substring(2));
				}
			}});
		List<JComponent> logs = createNotificationChannels();

		Iterator<NotificationChannel> it = channelManager.getChannels().iterator();
		for (JComponent tab : logs) {
			tabs.add(it.next().getName(), tab);
		}
		channelManager.addHiddenChannelListener(new NotificationChannelManager.HiddenChannelListener() {
			@Override
			public void channelModified(int index) {
				// Mark the tab as modified so that the user can see there's
				// new text
				String channelName = tabs.getTitleAt(index);
				if (!channelName.startsWith("* ")) {
					tabs.setTitleAt(index, "* " + channelName);
				}
			}
		});

		return tabs;
	}

	/**
	 * Create chat channels.
	 *
	 * @return Chat log components of the notification channels
	 */
	private List<JComponent> createNotificationChannels() {
		List<JComponent> list = new ArrayList<JComponent>();
		channelManager = new NotificationChannelManager();
		KTextEdit edit = new KTextEdit();
		list.add(edit);

		// ** Main channel **
		// Compatibility hack. Sets the default string for main channel from
		// the old configuration values.
		String mainDefault = "";
		WtWindowManager wm = WtWindowManager.getInstance();
		if (!Boolean.parseBoolean(wm.getProperty("ui.healingmessage", "false"))) {
			mainDefault += NotificationType.HEAL;
		}
		if (!Boolean.parseBoolean(wm.getProperty("ui.poisonmessage", "false"))) {
			if (!"".equals(mainDefault)) {
				mainDefault += ",";
			}
			mainDefault += NotificationType.POISON;
		}

		channelManager.addChannel(new NotificationChannel("Główny", edit, true, mainDefault));
		
		// ** Private channel **
		edit = new KTextEdit();
		edit.setChannelName("Prywatny");
		/*
		 * Give it a different background color to make it different from the
		 * main chat log.
		 */
		edit.setDefaultBackground(Color.decode(PRIVATE_TAB_COLOR));
		list.add(edit);
		/*
		 * Types shown by default in the private/group tab. Admin messages
		 * should occur everywhere, of course, and not be possible to be
		 * disabled in preferences.
		 */
		String personalDefault = NotificationType.PRIVMSG.toString() + ","
				+ NotificationType.CLIENT + "," + NotificationType.GROUP + "," 
				+ NotificationType.TUTORIAL + "," + NotificationType.SUPPORT;
		channelManager.addChannel(new NotificationChannel("Prywatny", edit, false, personalDefault));
		
		return list;
	}

	/**
	 * Get the main window component.
	 *
	 * @return main window
	 */
	public Frame getMainFrame() {
		return mainFrame.getMainFrame();
	}

	/**
	 * Get the current game screen height.
	 *
	 * @return The height.
	 */
	public int getHeight() {
		return screen.getHeight();
	}

	/**
	 * Get the current game screen width.
	 *
	 * @return The width.
	 */
	public int getWidth() {
		return screen.getWidth();
	}



	/**
	 * Set the input chat line text.
	 *
	 * @param text
	 *            The text.
	 */
	public void setChatLine(final String text) {
		chatText.setChatLine(text);

	}

	/**
	 * Clear the visible channel log.
	 */
	public void clearGameLog() {
		channelManager.getVisibleChannel().clear();
	}

	/**
	 * Set the user's position.
	 *
	 * @param x
	 *            The user's X coordinate.
	 * @param y
	 *            The user's Y coordinate.
	 */
	public void setPosition(final double x, final double y) {
		positionChangeListener.positionChanged(x, y);
	}

	/**
	 * Sets the offline indication state.
	 *
	 * @param offline
	 *            <code>true</code> if offline.
	 */
	public void setOffline(final boolean offline) {
		screenController.setOffline(offline);
		this.offline = offline;
	}

	public void requestQuit() {
		if (client.getConnectionState() || !offline) {
			quitDialog.requestQuit();
		} else {
			System.exit(0);
		}
	}

	public IPerceptionListener getPerceptionListener() {
		return perceptionListener;
	}

	/**
	 * Get the client.
	 *
	 * @return The client.
	 */
	public StendhalClient getClient() {
		return client;
	}

	/**
	 * The layered pane where the game screen is does not automatically resize
	 * the game screen. This handler is needed to do that work.
	 */
	private static class SplitPaneResizeListener extends ComponentAdapter {
		private final Component child;

		public SplitPaneResizeListener(Component child) {
			this.child = child;
		}

		@Override
		public void componentResized(ComponentEvent e) {
			// Pass on resize event
			child.setSize(e.getComponent().getSize());
		}
	}

	/**
	 * sets the cursor
	 *
	 * @param cursor Cursor
	 */
	public void setCursor(Cursor cursor) {
		pane.setCursor(cursor);
	}

	/**
	 * gets the sound system
	 *
	 * @return SoundSystemFacade
	 */
	@Override
	public SoundSystemFacade getSoundSystemFacade() {
		if (soundSystemFacade == null) {
			try {
				if ((DataLoader.getResource("data/sound/harp-1.ogg") != null)
						|| (DataLoader.getResource("data/music/the_old_tavern.ogg") != null)) {
					soundSystemFacade = new games.stendhal.client.sound.sound.SoundSystemFacadeImpl();
				} else {
					soundSystemFacade = new NoSoundFacade();
				}
			} catch (RuntimeException e) {
				soundSystemFacade = new NoSoundFacade();
				logger.error(e, e);
			}
		}
		return soundSystemFacade;
	}

	public void switchToSpellState(RPObject spell) {
		this.screen.switchToSpellCastingState(spell);
	}
}
