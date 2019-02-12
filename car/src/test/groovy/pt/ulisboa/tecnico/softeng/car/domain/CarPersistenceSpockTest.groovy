package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate

import pt.ist.fenixframework.FenixFramework

class CarPersistenceSpockTest extends SpockPersistenceTestAbstractClass {

	private static final String ADVENTURE_ID = "AdventureId";
	private static final String NAME1 = "eartz";
	private static final String PLATE_CAR1 = "aa-00-11";
	private static final String PLATE_CAR2 = "aa-00-12";
	private static final String DRIVING_LICENSE = "br123";
	private static final LocalDate date1 = LocalDate.parse("2018-01-06");
	private static final LocalDate date2 = LocalDate.parse("2018-01-07");
	private static final String NIF = "NIF";
	private static final String IBAN = "IBAN";
	private static final String IBAN_BUYER = "IBAN";

	@Override
	def whenCreateInDatabase() {
		RentACar rentACar = new RentACar(NAME1, NIF, IBAN)
		Car car = new Car(PLATE_CAR1, 10, 10, rentACar)
		Motorcycle motorcycle = new Motorcycle(PLATE_CAR2, 20, 5, rentACar)
		car.rent(DRIVING_LICENSE, date1, date2, NIF, IBAN_BUYER, ADVENTURE_ID)
	}

	@Override
	def thenAssert() {
		assert FenixFramework.getDomainRoot().getRentACarSet().size() == 1;

		RentACar rentACar = new ArrayList<>(FenixFramework.getDomainRoot().getRentACarSet()).get(0)
		assert rentACar.getVehicleSet().size() == 2;
		Processor processor = rentACar.getProcessor()
		assert rentACar.getName().equals(NAME1);
		assert rentACar.getNif().equals(NIF);
		assert rentACar.getIban().equals(IBAN);
		assert processor != null;
		assert processor.getRentingSet().size() == 1;

		for (Vehicle vehicle : rentACar.getVehicleSet()) {
			if (vehicle instanceof Car) {
				assert vehicle.getPlate().equals(PLATE_CAR1.toUpperCase());
				assert 10, vehicle.getKilometers().intValue() == 10;
				assert vehicle.getPrice() == 10;
			}
			if (vehicle instanceof Motorcycle) {
				assert vehicle.getPlate().equals(PLATE_CAR2.toUpperCase());
				assert vehicle.getKilometers().intValue() == 20;
				assert vehicle.getPrice() == 5;
			}
		}

		for (Vehicle vehicle : rentACar.getVehicleSet()) {
			if (vehicle instanceof Car) {
				assert vehicle.getRentingSet().size() == 1;
				Renting renting = new ArrayList<>(vehicle.getRentingSet()).get(0);
				assert renting.getDrivingLicense().equals(DRIVING_LICENSE);
				assert renting.getBegin() == date1;
				assert renting.getEnd() == date2;
				assert renting.getClientNif().equals(NIF);
				assert renting.getClientIban().equals(IBAN);
				assert renting.getTime() != null;
				assert renting.getProcessor() != null;
			}
			if (vehicle instanceof Motorcycle) {
				assert vehicle.getRentingSet().size() == 0;
			}
		}
	}

	@Override
	def deleteFromDatabase() {
		for (RentACar ra : FenixFramework.getDomainRoot().getRentACarSet()) {
			ra.delete();
		}
	}
}
