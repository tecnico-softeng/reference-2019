package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate

class RentACarGetAllAvailableVehiclesSpockTest extends SpockRollbackTestAbstractClass {
	private static final String ADVENTURE_ID = "AdventureId"
	private static final String NAME1='eartz'
	private static final String NAME2='eartz'
	private static final String PLATE_CAR1='aa-00-11'
	private static final String PLATE_CAR2='aa-00-22'
	private static final String PLATE_MOTORCYCLE='44-33-HZ'
	private static final String DRIVING_LICENSE='br123'
	private static final LocalDate date1= LocalDate.parse('2018-01-06')
	private static final LocalDate date2= LocalDate.parse('2018-01-07')
	private static final LocalDate date3= LocalDate.parse('2018-01-08')
	private static final LocalDate date4= LocalDate.parse('2018-01-09')
	private static final String NIF='NIF'
	private static final String IBAN='IBAN'
	private static final String IBAN_BUYER='IBAN'
	private RentACar rentACar1
	private RentACar rentACar2

	@Override
	def populate4Test() {
		this.rentACar1=new RentACar(NAME1,NIF,IBAN)

		this.rentACar2=new RentACar(NAME2,NIF + '1',IBAN)

	}

	def 'only cars'() {
		given:
        Vehicle car1=new Car(PLATE_CAR1,10,10,this.rentACar1)

		car1.rent(DRIVING_LICENSE,date1,date2,NIF,IBAN_BUYER,ADVENTURE_ID)

        Vehicle car2=new Car(PLATE_CAR2,10,10,this.rentACar2)

        Vehicle motorcycle=new Motorcycle(PLATE_MOTORCYCLE,10,10,this.rentACar1)

		Set<Vehicle> cars= RentACar.getAllAvailableCars(date3,date4)

		expect:
		cars.contains(car1)
		cars.contains(car2)
		!cars.contains(motorcycle)
	}

	def 'only available cars'() {
		given:
        Vehicle car1=new Car(PLATE_CAR1,10,10,this.rentACar1)

        Vehicle car2=new Car(PLATE_CAR2,10,10,this.rentACar2)

		car1.rent(DRIVING_LICENSE,date1,date2,NIF,IBAN_BUYER,ADVENTURE_ID)

		Set<Vehicle> cars= RentACar.getAllAvailableCars(date1,date2)

		expect:
		!cars.contains(car1)
		cars.contains(car2)
	}

	def 'only motorcycles'() {
		given:
        Vehicle car=new Car(PLATE_CAR1,10,10,this.rentACar1)

        Vehicle motorcycle=new Motorcycle(PLATE_MOTORCYCLE,10,10,this.rentACar1)

		Set<Vehicle> cars= RentACar.getAllAvailableMotorcycles(date3,date4)

		expect:
		cars.contains(motorcycle)
		!cars.contains(car)
	}

}
