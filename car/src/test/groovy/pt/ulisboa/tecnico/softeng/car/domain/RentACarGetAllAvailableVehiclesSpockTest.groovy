package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate

class RentACarGetAllAvailableVehiclesSpockTest extends SpockRollbackTestAbstractClass {
	def ADVENTURE_ID = "AdventureId"
	def NAME1='eartz'
	def NAME2='eartz'
	def PLATE_CAR1='aa-00-11'
	def PLATE_CAR2='aa-00-22'
	def PLATE_MOTORCYCLE='44-33-HZ'
	def DRIVING_LICENSE='br123'
	def date1= LocalDate.parse('2018-01-06')
	def date2= LocalDate.parse('2018-01-07')
	def date3= LocalDate.parse('2018-01-08')
	def date4= LocalDate.parse('2018-01-09')
	def NIF='NIF'
	def IBAN='IBAN'
	def IBAN_BUYER='IBAN'
	def rentACar1
	def rentACar2

	@Override
	def populate4Test() {
		this.rentACar1=new RentACar(NAME1,NIF,IBAN)
		this.rentACar2=new RentACar(NAME2,NIF + '1',IBAN)
	}

	def 'only cars'() {
		when:
        Vehicle car1=new Car(PLATE_CAR1,10,10,this.rentACar1)
		car1.rent(DRIVING_LICENSE,date1,date2,NIF,IBAN_BUYER,ADVENTURE_ID)
        Vehicle car2=new Car(PLATE_CAR2,10,10,this.rentACar2)

        Vehicle motorcycle=new Motorcycle(PLATE_MOTORCYCLE,10,10,this.rentACar1)
		Set<Vehicle> cars= RentACar.getAllAvailableCars(date3,date4)

		then:
		cars.contains(car1)
		cars.contains(car2)
		!cars.contains(motorcycle)
	}

	def 'only available cars'() {
		given:
        Vehicle car1=new Car(PLATE_CAR1,10,10,this.rentACar1)
        Vehicle car2=new Car(PLATE_CAR2,10,10,this.rentACar2)

		car1.rent(DRIVING_LICENSE,date1,date2,NIF,IBAN_BUYER,ADVENTURE_ID)

		when:
		Set<Vehicle> cars= RentACar.getAllAvailableCars(date1,date2)

		then:
		!cars.contains(car1)
		cars.contains(car2)
	}

	def 'only motorcycles'() {
		given:
        Vehicle car=new Car(PLATE_CAR1,10,10,this.rentACar1)
        Vehicle motorcycle=new Motorcycle(PLATE_MOTORCYCLE,10,10,this.rentACar1)

		when:
		Set<Vehicle> cars= RentACar.getAllAvailableMotorcycles(date3,date4)

		then:
		cars.contains(motorcycle)
		!cars.contains(car)
	}

}
