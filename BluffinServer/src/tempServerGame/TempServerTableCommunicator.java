package tempServerGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import pokerLogic.OldPokerPlayerInfo;
import pokerLogic.PokerPlayerAction;
import pokerLogic.Pot;
import pokerLogic.TypePlayerAction;
import pokerLogic.TypePokerGame;
import pokerLogic.TypePokerRound;
import protocolGame.GameTableInfoCommand;
import protocolLobby.LobbyCreateTableCommand;
import tempServerGameTools.TempServerPokerObserver;
import utility.Constants;
import utility.IClosingListener;

/**
 * @author Hocus
 *         This class represent a Hold'Em Table.
 *         It simulate all the poker game of the table.
 */
public class TempServerTableCommunicator implements Runnable
{
    
    private final TempServerPokerObserver m_pokerObserver = new TempServerPokerObserver();
    /*
     * ######################################################
     * Class Variables
     * ######################################################
     */
    private final TempServerPokerTableInfo m_info;
    
    // Listeners
    private final List<IClosingListener<TempServerTableCommunicator>> m_closingListeners = Collections.synchronizedList(new ArrayList<IClosingListener<TempServerTableCommunicator>>());
    
    // Thread variables
    private boolean m_waitForFirstPlayer;
    private boolean m_isRunning = false;
    private boolean m_stopTable;
    
    // Game variables
    
    /*
     * ######################################################
     * Constructors
     * ######################################################
     */

    /**
     * Create a new table.
     * The start method must be call to start the table thread.
     * 
     * @param p_name
     *            Name of the table
     * @param p_gameType
     *            Hold'Em Poker type
     * @param p_bigBlind
     *            Amount of the Big Blind
     * @param p_playerCapacity
     *            Number of seat available
     */
    
    public TempServerTableCommunicator(LobbyCreateTableCommand command)
    {
        m_stopTable = false;
        m_info = new TempServerPokerTableInfo(command);
        
        m_isRunning = true;
        m_waitForFirstPlayer = true;
    }
    
    /*
     * ######################################################
     * Getters and Setters
     * ######################################################
     */

    /**
     * Add a new ClosingListener. The listener is notify when the table close
     * 
     * @param p_listener
     *            The new listener
     */
    public void addClosingListener(IClosingListener<TempServerTableCommunicator> p_listener)
    {
        m_closingListeners.add(p_listener);
    }
    
    /**
     * Deal the holes card.
     */
    private void dealHole()
    {
        final int firstPlayer = m_info.nextPlayingPlayer(m_info.m_noSeatDealer);
        m_info.m_playerTurn = firstPlayer;
        
        do
        {
            m_info.getPlayer(m_info.m_playerTurn).setHand(m_info.m_deck.pop(), m_info.m_deck.pop());
            
            m_pokerObserver.holeCardDeal(this, (TempServerPokerPlayerInfo) m_info.getPlayer(m_info.m_playerTurn));
            m_info.m_playerTurn = m_info.nextPlayingPlayer(m_info.m_playerTurn);
        }
        while (m_info.m_playerTurn != firstPlayer);
        
        try
        {
            Thread.sleep(Constants.SERVER_WAIT_TIME);
        }
        catch (final InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * End a game
     */
    private void endGame()
    {
        m_info.endGame();
        
        m_pokerObserver.gameEnded(this);
        
        try
        {
            Thread.sleep(Constants.SERVER_WAIT_TIME);
        }
        catch (final InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * return the value of the big blind
     * 
     * @return
     *         the big blind amount
     */
    public int getBigBlind()
    {
        return m_info.m_bigBlindAmount;
    }
    
    /**
     * Return the state of the game (Preflop, flop, turn ...)
     * 
     * @return
     *         The state of the game.
     */
    public TypePokerRound getGameState()
    {
        return m_info.m_gameState;
    }
    
    /**
     * Return the type of the game
     * 
     * @return
     *         The type of the game
     */
    public TypePokerGame getGameType()
    {
        return m_info.m_gameType;
    }
    
    /**
     * Return the name of the table
     * 
     * @return
     *         The name of the table
     */
    public String getName()
    {
        return m_info.m_name;
    }
    
    /*
     * ######################################################
     * Public Methods
     * ######################################################
     */

    /**
     * Return the number of player sitting on the table
     * 
     * @return
     *         The number of player
     */
    public int getNbPlayers()
    {
        return m_info.getNbPlayers();
    }
    
    /**
     * Return the total number of seat
     * 
     * @return
     *         the total number of seat
     */
    public int getNbSeats()
    {
        return m_info.getNbSeats();
    }
    
    /**
     * Get the next available seat
     * 
     * @return
     *         The next available seat
     */
    public synchronized int getNextSeat()
    {
        if (m_info.getNbPlayers() >= m_info.getNbSeats())
        {
            return -1;
        }
        int i = 0;
        while (m_info.getPlayer(i) != null)
        {
            ++i;
        }
        
        return i;
    }
    
    /**
     * Return the seat number of the Big Blind in the current game
     * 
     * @return
     *         The Big Blind's seat
     */
    public int getNoSeatBigBlind()
    {
        return m_info.m_noSeatBigBlind;
    }
    
    /**
     * Return the seat number of the dealer in the current game
     * 
     * @return
     *         The dealer's seat
     */
    public int getNoSeatDealer()
    {
        return m_info.m_noSeatDealer;
    }
    
    /**
     * Return the seat number of the Small Blind in the current game
     * 
     * @return
     *         The Small Blind's seat
     */
    public int getNoSeatSmallBlind()
    {
        return m_info.m_noSeatSmallBlind;
    }
    
    /**
     * Return the list of player of this table.
     * 
     * @return
     *         The list of player
     */
    public List<OldPokerPlayerInfo> getPlayers()
    {
        return m_info.getPlayers();
    }
    
    /**
     * Return the pots on the table
     * 
     * @return
     *         The pots
     */
    public Stack<Pot> getPots()
    {
        return m_info.m_pots;
    }
    
    /*
     * ######################################################
     * Private Methods
     * ######################################################
     */

    /**
     * Return the total amount of chips on the table.
     * This include the pots and the bets of the players.
     * It does NOT include the chips each player own.
     * 
     * @return
     *         The amount of chips on the table
     */
    public int getTotalPot()
    {
        return m_info.m_totalPotAmount;
    }
    
    /**
     * Initialize a game.
     * Called at the start of each game.
     * 
     * @return
     */
    private boolean initializeGame()
    {
        m_info.initializeGame();
        // check if we can start a new game.
        boolean canStartGame = false;
        while (!canStartGame)
        {
            int nbPlayingPlayer = 0;
            
            if (m_info.getNbPlayers() > 0)
            {
                for (int i = 0; i < m_info.getNbSeats(); i++)
                {
                    if (m_info.getPlayer(i) != null)
                    {
                        if (((TempServerPokerPlayerInfo) m_info.getPlayer(i)).sitOutNextHand())
                        {
                            final OldPokerPlayerInfo deletedPlayer = m_info.getPlayer(i);
                            m_info.removePlayer(i);
                            
                            m_pokerObserver.playerLeftTable(this, (TempServerPokerPlayerInfo) deletedPlayer);
                        }
                        else if (m_info.getPlayer(i).getMoney() > 0)
                        {
                            if (m_info.getPlayer(i).canStartGame())
                            {
                                nbPlayingPlayer++;
                            }
                        }
                    }
                }
            }
            
            if (m_stopTable || ((m_info.getNbPlayers() <= 0) && !m_waitForFirstPlayer))
            {
                return false;
            }
            
            if (nbPlayingPlayer >= 2)
            {
                canStartGame = true;
            }
            else
            {
                // m_pokerObserver.waitingForPlayers(this);
                try
                {
                    Thread.sleep(Constants.SERVER_WAIT_TIME_FOR_NEW_PLAYERS);
                }
                catch (final InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        m_info.startGame();
        
        // notify the observer that a new game is starting
        m_pokerObserver.gameStarted(this);
        
        try
        {
            Thread.sleep(Constants.SERVER_WAIT_TIME);
        }
        catch (final InterruptedException e)
        {
            e.printStackTrace();
        }
        
        return true;
    }
    
    /**
     * Initialize the table.
     * Called when the table is started
     */
    private void initializeTable()
    {
        m_info.m_noSeatDealer = m_info.getNbSeats() - 1;
        m_pokerObserver.tableStarted(this);
    }
    
    /**
     * Return True if the table is running
     * 
     * @return
     *         True if the table is running
     */
    public boolean isRunning()
    {
        return m_isRunning;
    }
    
    /**
     * Add a player to the game.
     * 
     * @param p_player
     *            The player to add.
     * @param p_noSeat
     *            The seat number of the player
     */
    public void join(TempServerPokerPlayerInfo p_player, int p_noSeat)
    {
        m_waitForFirstPlayer = false;
        m_info.addPlayer(p_noSeat, p_player);
        p_player.setTable(this);
        p_player.setNoSeat(p_noSeat);
        m_pokerObserver.playerJoinedTable(this, p_player);
        m_pokerObserver.tableInfos(this);
    }
    
    /**
     * Create a string that represent the table.
     * This method is used to send the table over the network
     * 
     * @param p_player
     *            The player that will received the table informations
     * @return
     *         A string representing the table
     */
    public GameTableInfoCommand buildCommand(TempServerPokerPlayerInfo p_player)
    {
        return m_info.buildCommand(p_player);
    }
    
    /**
     * The last player the pot
     */
    private void onePlayerStanding()
    {
        OldPokerPlayerInfo winner;
        
        final Iterator<OldPokerPlayerInfo> it = m_info.m_pots.peek().getParticipant().iterator();
        winner = it.next();
        
        while (!m_info.m_pots.empty())
        {
            final Pot pot = m_info.m_pots.pop();
            
            winner.winPot(pot.getAmount());
            m_pokerObserver.potWon(this, (TempServerPokerPlayerInfo) winner, pot, pot.getAmount());
            
            try
            {
                Thread.sleep(Constants.SERVER_WAIT_TIME_SHOWDOWN);
            }
            catch (final InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Place the blinds on the table
     */
    private void placeBlinds()
    {
        m_info.placeSmallBlind();
        m_pokerObserver.smallBlindPosted(this, (TempServerPokerPlayerInfo) m_info.getPlayer(m_info.m_noSeatSmallBlind), m_info.m_currentBet);
        try
        {
            Thread.sleep(Constants.SERVER_WAIT_TIME);
        }
        catch (final InterruptedException e)
        {
            e.printStackTrace();
        }
        
        m_info.placeBigBlind();
        m_pokerObserver.bigBlindPosted(this, (TempServerPokerPlayerInfo) m_info.getPlayer(m_info.m_noSeatBigBlind), m_info.getPlayer(m_info.m_noSeatBigBlind).getBet());
        try
        {
            Thread.sleep(Constants.SERVER_WAIT_TIME);
        }
        catch (final InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Remove a ClosingListener from the list of listener
     * 
     * @param p_listener
     *            The listener to remove
     */
    public void removeClosingListener(IClosingListener<TempServerTableCommunicator> p_listener)
    {
        m_closingListeners.remove(p_listener);
    }
    
    /**
     * Start the table's loop
     */
    public void run()
    {
        initializeTable();
        while (initializeGame())
        {
            placeBlinds();
            dealHole();
            if (startBettingRound())
            {
                m_info.dealFlop();
                m_pokerObserver.flopDealt(this, m_info.getBoard());
                if (startBettingRound())
                {
                    m_info.dealTurn();
                    m_pokerObserver.turnDeal(this, m_info.getBoard());
                    if (startBettingRound())
                    {
                        m_info.dealRiver();
                        m_pokerObserver.riverDeal(this, m_info.getBoard());
                        if (startBettingRound())
                        {
                            showdown();
                        }
                    }
                }
            }
            endGame();
        }
        
        m_isRunning = false;
        
        m_pokerObserver.gameEnded(this);
        
        synchronized (m_closingListeners)
        {
            for (final IClosingListener<TempServerTableCommunicator> listener : m_closingListeners)
            {
                listener.closing(this);
            }
        }
    }
    
    /**
     * Show the hands of all remaining player
     */
    private void showAllHands()
    {
        for (int i = 0; i < m_info.getNbSeats(); i++)
        {
            final TempServerPokerPlayerInfo player = (TempServerPokerPlayerInfo) m_info.getPlayer(i);
            if (!(player == null) && player.isPlaying() && !player.isFolded() && !player.isShowingCard())
            {
                player.showCards();
                m_pokerObserver.playerShowCard(this, player);
            }
        }
    }
    
    /**
     * Check the winners and distribute the pots
     */
    private void showdown()
    {
        m_info.m_gameState = TypePokerRound.SHOWDOWN;
        
        showAllHands();
        
        final Vector<OldPokerPlayerInfo> bestPlayers = new Vector<OldPokerPlayerInfo>();
        long bestHand = 0;
        
        while (!m_info.m_pots.empty())
        {
            final Pot pot = m_info.m_pots.pop();
            if (pot.getParticipant().size() > 0)
            {
                final Iterator<OldPokerPlayerInfo> it = pot.getParticipant().iterator();
                while (it.hasNext())
                {
                    final OldPokerPlayerInfo player = it.next();
                    final long handValue = player.handValue(m_info.getBoard());
                    
                    if (handValue > bestHand)
                    {
                        bestPlayers.removeAllElements();
                        bestPlayers.add(player);
                        bestHand = handValue;
                    }
                    else if (handValue == bestHand)
                    {
                        bestPlayers.add(player);
                    }
                }
                
                final int amountPerWinner = pot.getAmount() / bestPlayers.size();
                for (int i = 0; i < bestPlayers.size(); i++)
                {
                    bestPlayers.elementAt(i).winPot(amountPerWinner);
                    m_pokerObserver.potWon(this, (TempServerPokerPlayerInfo) bestPlayers.elementAt(i), pot, amountPerWinner);
                    
                    try
                    {
                        Thread.sleep(Constants.SERVER_WAIT_TIME_SHOWDOWN);
                    }
                    catch (final InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    /**
     * Start the table.
     * This method creates a new thread.
     */
    public void start()
    {
        new Thread(this).start();
    }
    
    /**
     * Start a betting round.
     * 
     * @return
     *         True if the game can continue.
     */
    private boolean startBettingRound()
    {
        m_info.m_firstTurn = true;
        int lastPlayer = m_info.m_noSeatDealer;
        if ((m_info.m_nbPlayingPlayers == 2) && (m_info.m_gameState == TypePokerRound.PREFLOP))
        {
            lastPlayer = m_info.nextPlayingPlayer(m_info.m_noSeatDealer);
        }
        else if ((m_info.m_nbPlayingPlayers > 2) && (m_info.m_gameState == TypePokerRound.PREFLOP))
        {
            lastPlayer = getNoSeatBigBlind();
        }
        m_info.m_playerTurn = m_info.nextPlayingPlayer(lastPlayer);
        m_info.m_bettingPlayer = m_info.m_playerTurn;
        final TreeSet<Integer> bets = new TreeSet<Integer>();
        
        // Check if the blinds are all-in
        if (m_info.m_gameState == TypePokerRound.PREFLOP)
        {
            OldPokerPlayerInfo blind = m_info.getPlayer(m_info.m_noSeatSmallBlind);
            if (blind.isAllIn() && !bets.contains(blind.getBet()))
            {
                bets.add(blind.getBet());
            }
            blind = m_info.getPlayer(m_info.m_noSeatBigBlind);
            if (blind.isAllIn() && !bets.contains(blind.getBet()))
            {
                bets.add(blind.getBet());
            }
        }
        
        // Ask each player to take a decision until they can't bet.
        TempServerPokerPlayerInfo player;
        PokerPlayerAction action;
        while (m_info.continueBetting())
        {
            player = (TempServerPokerPlayerInfo) m_info.getPlayer(m_info.m_playerTurn);
            // make sure the player can take an action
            if (!(player.isFolded() || player.isAllIn()))
            {
                final int oldBet = player.getBet();
                m_pokerObserver.playerTurnStarted(this, player);
                
                // Ask an action to the player
                action = player.takeAction(m_info.m_currentBet, m_info.getMinimumRaise(player), m_info.getMaximumRaise(player));
                
                m_info.m_totalPotAmount += (player.getBet() - oldBet);
                
                if (player.isAllIn())
                {
                    m_info.m_nbAllIn++;
                    m_info.m_nbBetting--;
                }
                else if (player.isFolded())
                {
                    m_info.m_nbFolded++;
                    m_info.m_nbBetting--;
                }
                
                if (action.getType() == TypePlayerAction.RAISE)
                {
                    m_info.m_currentBet = player.getBet();
                    m_info.m_noSeatLastRaiser = m_info.m_playerTurn;
                }
                
                if (player.isAllIn() && !bets.contains(player.getBet()))
                {
                    bets.add(player.getBet());
                }
                
                if ((m_info.m_noSeatLastRaiser < 0) && !player.isFolded())
                {
                    m_info.m_noSeatLastRaiser = m_info.m_playerTurn;
                }
                
                m_pokerObserver.playerEndTurn(this, player, action);
                
                try
                {
                    Thread.sleep(Constants.SERVER_WAIT_TIME);
                }
                catch (final InterruptedException e)
                {
                    e.printStackTrace();
                }
                
            }
            
            if (m_info.m_firstTurn && (m_info.m_playerTurn == lastPlayer))
            {
                m_info.m_firstTurn = false;
            }
            
            m_info.m_playerTurn = m_info.nextPlayingPlayer(m_info.m_playerTurn);
            m_info.m_bettingPlayer = m_info.m_playerTurn;
        }
        
        // Manage pots
        m_info.endBettingRound(bets);
        
        m_pokerObserver.endBettingTurn(this);
        
        if (m_info.m_nbBetting < 2)
        {
            // if there is only one player left
            if ((m_info.m_nbAllIn <= 0) || ((m_info.m_nbBetting <= 0) && (m_info.m_nbAllIn == 1)))
            {
                onePlayerStanding();
                return false;
            }
            else
            {
                // If all the players are all-in but one or if they are all all-in
                showAllHands();
            }
        }
        
        // Make sure that no pot can be won only by one player
        if (!m_info.m_pots.isEmpty() && (m_info.m_pots.peek().getParticipant().size() == 1))
        {
            final Pot uselessPot = m_info.m_pots.pop();
            final OldPokerPlayerInfo playerInPot = uselessPot.getParticipant().firstElement();
            
            playerInPot.addMoney(uselessPot.getAmount());
            m_info.m_pots.peek().addParticipant(playerInPot);
            
            m_pokerObserver.playerMoneyChanged(this, (TempServerPokerPlayerInfo) playerInPot);
        }
        
        return true;
    }
    
    /**
     * Stop the table.
     * The table will finish the current game before closing.
     */
    public void stop()
    {
        m_stopTable = true;
    }
    
    public TempServerPokerObserver getPokerObserver()
    {
        return m_pokerObserver;
    }
}
