package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface


class CarPersistenceSpockTest extends SpockPersistenceTestAbstractClass {

    def ADVENTURE_ID = 'AdventureId'
    def NAME1 = 'eartz'
    def PLATE_CAR1 = 'aa-00-11'
    def PLATE_CAR2 = 'aa-00-12'
    def DRIVING_LICENSE = 'br123'
    def date1 = LocalDate.parse('2018-01-06')
    def date2 = LocalDate.parse('2018-01-07')
    def NIF = 'NIF'
    def IBAN = 'IBAN'
    def IBAN_BUYER = 'IBAN'

    @Override
    def whenCreateInDatabase() {
        def bankInterface = new BankInterface()
        def taxInterface = new TaxInterface()
        def processor = new Processor(bankInterface, taxInterface)

        def rentACar = new RentACar(NAME1, NIF, IBAN, processor)
        def car = new Car(PLATE_CAR1, 10, 10, rentACar)
        car.rent(DRIVING_LICENSE, date1, date2, NIF, IBAN_BUYER, ADVENTURE_ID)
    }

    @Override
    def thenAssert() {
        FenixFramework.getDomainRoot().getRentACarSet().size() == 1

        def rentACar = new ArrayList<>(FenixFramework.getDomainRoot().getRentACarSet()).get(0)
        rentACar.getVehicleSet().size() == 2
        def processor = rentACar.getProcessor()
        rentACar.getName().equals(NAME1)
        rentACar.getNif().equals(NIF)
        rentACar.getIban().equals(IBAN)
        processor != null
        processor.getRentingSet().size() == 1

        for (def vehicle : rentACar.getVehicleSet()) {
            if (vehicle instanceof Car) {
                vehicle.getPlate().equals(PLATE_CAR1.toUpperCase())
                vehicle.getKilometers().intValue() == 10
                vehicle.getPrice() == 10
            }
            if (vehicle instanceof Motorcycle) {
                vehicle.getPlate().equals(PLATE_CAR2.toUpperCase())
                vehicle.getKilometers().intValue() == 20
                vehicle.getPrice() == 5
            }
        }

        for (def vehicle : rentACar.getVehicleSet()) {
            if (vehicle instanceof Car) {
                vehicle.getRentingSet().size() == 1
                def renting = new ArrayList<>(vehicle.getRentingSet()).get(0)
                renting.getDrivingLicense().equals(DRIVING_LICENSE)
                renting.getBegin() == date1
                renting.getEnd() == date2
                renting.getClientNif().equals(NIF)
                renting.getClientIban().equals(IBAN)
                renting.getTime() != null
                renting.getProcessor() != null
            }
            if (vehicle instanceof Motorcycle) {
                vehicle.getRentingSet().size() == 0
            }
        }
    }

    @Override
    def deleteFromDatabase() {
        for (def ra : FenixFramework.getDomainRoot().getRentACarSet()) {
            ra.delete()
        }
    }
}
