package ttr.view.gameComponents;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;

import edu.virginia.engine.display.DisplayObjectContainer;
import edu.virginia.engine.display.Sprite;
import ttr.model.destinationCards.Destination;
import ttr.model.destinationCards.Route;
import ttr.model.destinationCards.Routes;
import ttr.model.events.ClaimRouteClickEvent;
import ttr.model.trainCards.TrainCardColor;

/**
 * The panel that is used to select a route that a human player wants to claim
 * contains two combo boxes and a button for confirming
 * */
public class RouteSelectionPanel extends Sprite implements ActionListener{

	/* Two combo boxes for selecting to and from cities */
	private JComboBox<Destination> to;
	private JComboBox<Destination> from;
	
	/* Which card color the user wishes to use */
	private JComboBox<TrainCardColor> colors;
	
	/* Confirmation button */
	private JButton confirmButton;
	
	public RouteSelectionPanel() {
		super("ROUTE_SELECTION_PANEL");
		
		/* Get list of destinations */
		Destination[] destinations = Destination.values();
		
		/* setup the combo boxes */
		to = new JComboBox<Destination>(destinations);
		from = new JComboBox<Destination>(destinations);
		
		/* Setup color selector */
		colors = new JComboBox<TrainCardColor>(TrainCardColor.values());
		
		/* Setup the confirm button */
		confirmButton = new JButton("Claim This Route!");
		
		/* setup listener */
		confirmButton.addActionListener(this);
	}
	
	@Override
	protected void onAddedToStage(DisplayObjectContainer parent) {
		super.onAddedToStage(parent);
		
		/* Add it all to this sprite */
		this.addComponent(from, this.getX() + 0, this.getY() + 0, 200, 30);
		this.addComponent(to, (int)(this.getX() + from.getBounds().getWidth() + 20), this.getY() + 0, 200, 30);
		this.addComponent(confirmButton, (int)(this.getX() + ((from.getBounds().getWidth() + to.getBounds().getWidth() + 10) / 2)-75), (int)(this.getY() + from.getBounds().getHeight() + 20), 150, 50);
		this.addComponent(colors, confirmButton.getX() + confirmButton.getWidth() + 10, confirmButton.getY(), 200, 30);
	}
	
	@Override
	public void draw(Graphics g){
		super.draw(g);
		
	}

	/**
	 * Should be invoked only when the confirm button is clicked
	 * */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		/* Get the two selected cities */
		Destination city1 = (Destination)from.getSelectedItem();
		Destination city2 = (Destination)to.getSelectedItem();
		
		ArrayList<Route> routes = Routes.getInstance().getRoutes(city1, city2);
		if(routes.size() == 0) System.out.println("NOT A VALID ROUTE");
		else System.out.println("YOU SELECTED A VALID ROUTE");
		
		/* Grab the color to be used */
		TrainCardColor colorToUse = (TrainCardColor)colors.getSelectedItem();
		
		for(Route route : routes){
			if(route.getColor() == colorToUse || route.getColor() == TrainCardColor.rainbow || colorToUse == TrainCardColor.rainbow){
				dispatchEvent(new ClaimRouteClickEvent(ClaimRouteClickEvent.CLAIM_ROUTE_CLICKED, this, route, colorToUse));
				break;
			}
		}
	}
	
	

}
