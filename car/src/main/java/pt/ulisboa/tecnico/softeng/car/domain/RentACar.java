package pt.ulisboa.tecnico.softeng.car.domain;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.LocalDate;

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.car.exception.CarException;

public class RentACar extends RentACar_Base {
	public RentACar(String name, String nif, String iban) {
		checkArguments(name, nif, iban);

		setCode(nif + Integer.toString(getCounter()));
		setName(name);
		setNif(nif);
		setIban(iban);

		setProcessor(new Processor());

		FenixFramework.getDomainRoot().addRentACar(this);
	}

	public void delete() {
		setRoot(null);

		getProcessor().delete();

		for (Vehicle vehicle : getVehicleSet()) {
			vehicle.delete();
		}

		deleteDomainObject();
	}

	private void checkArguments(String name, String nif, String iban) {
		if (name == null || name.isEmpty() || nif == null || nif.isEmpty() || iban == null || iban.isEmpty()) {

			throw new CarException();
		}

		for (final RentACar rental : FenixFramework.getDomainRoot().getRentACarSet()) {
			if (rental.getNif().equals(nif)) {
				throw new CarException();
			}
		}
	}

	public boolean hasVehicle(String plate) {
		return getVehicleSet().stream().anyMatch(v -> v.getPlate().equals(plate));
	}

	public Set<Vehicle> getAvailableVehicles(Class<?> cls, LocalDate begin, LocalDate end) {
		final Set<Vehicle> availableVehicles = new HashSet<>();
		for (final Vehicle vehicle : getVehicleSet()) {
			if (cls == vehicle.getClass() && vehicle.isFree(begin, end)) {
				availableVehicles.add(vehicle);
			}
		}
		return availableVehicles;
	}

	@Override
	public int getCounter() {
		int counter = super.getCounter() + 1;
		setCounter(counter);
		return counter;
	}

	public Renting getRenting4AdventureId(String adventureId) {
		return getVehicleSet().stream().flatMap(v -> v.getRentingSet().stream())
				.filter(r -> r.getAdventureId() != null && r.getAdventureId().equals(adventureId)).findFirst()
				.orElse(null);

	}

}
