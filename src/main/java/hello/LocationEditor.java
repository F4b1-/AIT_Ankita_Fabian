package hello;

import com.vaadin.data.Binder;
import com.vaadin.data.ValueProvider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Setter;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A simple example to introduce building forms. As your real application is probably much
 * more complicated than this example, you could re-use this form in multiple places. This
 * example component is only used in VaadinUI.
 * <p>
 * In a real world application you'll most likely using a common super class for all your
 * forms - less code, better UX. See e.g. AbstractForm in Viritin
 * (https://vaadin.com/addon/viritin).
 */
@SpringComponent
@UIScope
public class LocationEditor extends VerticalLayout {

	private final LocationRepository repository;

	/**
	 * The currently edited customer
	 */
	private Location location;

	/* Fields to edit properties in Customer entity */
	TextField name = new TextField("First name");
	TextField lat = new TextField("Latitude");
	TextField lon = new TextField("Longitude");
	LatLon latLon = new LatLon();

	/* Action buttons */
	Button save = new Button("Save", VaadinIcons.CHECK);
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete", VaadinIcons.TRASH);
	CssLayout actions = new CssLayout(save, cancel, delete);

	Binder<Location> binder = new Binder<>(Location.class);

	@Autowired
	public LocationEditor(LocationRepository repository) {
		this.repository = repository;

		addComponents(name, lat, lon, actions);

		// bind using naming convention
	//	binder.bindInstanceFields(this);

        binder.bind(name, Location::getName, Location::setName);

        binder.bind(lat,
                (ValueProvider<Location, String>) location -> String.valueOf(location.getLatLon().getLat()),
                (Setter<Location, String>) (location, lat) -> location.getLatLon().setLat(Double.parseDouble(lat)));

        // With explicit callback interface instances
        binder.bind(lon,
                (ValueProvider<Location, String>) location -> String.valueOf(location.getLatLon().getLon()),
                (Setter<Location, String>) (location, lon) -> location.getLatLon().setLon(Double.parseDouble(lon)));


		// Configure and style components
		setSpacing(true);
		actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		save.setStyleName(ValoTheme.BUTTON_PRIMARY);
		save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to save, delete and reset
		save.addClickListener(e -> repository.save(location));
		delete.addClickListener(e -> repository.delete(location));
		cancel.addClickListener(e -> editLocation(location));
		setVisible(false);
	}

	public interface ChangeHandler {

		void onChange();
	}

	public final void editLocation(Location l) {
		if (l == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = l.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			location = repository.findById(l.getId()).get();
		}
		else {
			location = l;
		}
		cancel.setVisible(persisted);

		// Bind customer properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
        binder.bind(name, Location::getName, Location::setName);

        binder.bind(lat,
                (ValueProvider<Location, String>) location -> String.valueOf(location.getLatLon().getLat()),
                (Setter<Location, String>) (location, lat) -> location.getLatLon().setLat(Double.parseDouble(lat)));

        // With explicit callback interface instances
        binder.bind(lon,
                (ValueProvider<Location, String>) location -> String.valueOf(location.getLatLon().getLon()),
                (Setter<Location, String>) (location, lon) -> location.getLatLon().setLon(Double.parseDouble(lon)));

		setVisible(true);

		// A hack to ensure the whole form is visible
		save.focus();
		// Select all text in firstName field automatically
		name.selectAll();
	}

	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either save or delete
		// is clicked
		save.addClickListener(e -> h.onChange());
		delete.addClickListener(e -> h.onChange());
	}

}
