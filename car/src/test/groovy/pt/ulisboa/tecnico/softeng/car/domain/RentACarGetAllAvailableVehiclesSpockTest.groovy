package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate

class RentACarGetAllAvailableVehiclesSpockTest extends SpockRollbackTestAbstractClass {
	def ADVENTURE_ID = "AdventureId"
	def NAME1 = 'eartz'
	def NAME2 = 'eartz'
	def PLATE_CAR1 = 'aa-00-11'
	def PLATE_CAR2 = 'aa-00-22'
	def PLATE_MOTORCYCLE = '44-33-HZ'
	def DRIVING_LICENSE = 'br123'
	def date1 = LocalDate.parse('2018-01-06')
	def date2 = LocalDate.parse('2018-01-07')
	def date3 = LocalDate.parse('2018-01-08')
	def date4 = LocalDate.parse('2018-01-09')
	def NIF = 'NIF'
	def IBAN = 'IBAN'
	def IBAN_BUYER = 'IBAN'
	def rentACar1
	def rentACar2

	@Override
	def populate4Test() {
		rentACar1 = new RentACar(NAME1,NIF,IBAN)
		rentACar2 = new RentACar(NAME2,NIF + '1',IBAN)
	}

	def 'only cars'() {
		given:
		def car1 = new Car(PLATE_CAR1,10,10,this.rentACar1)
		car1.rent(DRIVING_LICENSE,date1,date2,NIF,IBAN_BUYER,ADVENTURE_ID)
		def car2 = new Car(PLATE_CAR2,10,10,this.rentACar2)
		def motorcycle = new Motorcycle(PLATE_MOTORCYCLE,10,10,this.rentACar1)

		when:
		def cars = RentACar.getAllAvailableCars(date3,date4)

		then:
		cars.contains(car1)
		cars.contains(car2)
		!cars.contains(motorcycle)
	}

	def 'only available cars'() {
		given: 'creating two cars, and renting one'
		def car1 = new Car(PLATE_CAR1, 10, 10, rentACar1)
		def car2 = new Car(PLATE_CAR2, 10, 10, rentACar2)
		car1.rent(DRIVING_LICENSE, date1, date2, NIF, IBAN_BUYER, ADVENTURE_ID)

		when: 'when fetching available cars'
		def cars = RentACar.getAllAvailableCars(date1, date2)

		then: 'car2 should be in the returned list'
		!cars.contains(car1)
		cars.contains(car2)
	}

	def 'only motorcycles'() {
		given: 'creating one car, and one motorcycle'
		def car = new Car(PLATE_CAR1,10,10,this.rentACar1)
		def motorcycle = new Motorcycle(PLATE_MOTORCYCLE,10,10,this.rentACar1)

		when: 'when fetching available motorcycle'
		def cars = RentACar.getAllAvailableMotorcycles(date3,date4)

		then: 'only the motorcycle should be in the list'
		cars.contains(motorcycle)
		!cars.contains(car)
	}
}
