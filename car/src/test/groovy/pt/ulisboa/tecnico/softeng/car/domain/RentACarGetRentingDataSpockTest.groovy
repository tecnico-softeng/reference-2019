package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.car.exception.CarException
import pt.ulisboa.tecnico.softeng.car.services.local.dataobjects.RentingData

class RentACarGetRentingDataSpockTest extends SpockRollbackTestAbstractClass {
	def ADVENTURE_ID = "AdventureId"
	def NAME1='eartz'
	def PLATE_CAR1='aa-00-11'
	def DRIVING_LICENSE='br123'
	def date1= LocalDate.parse('2018-01-06')
	def date2= LocalDate.parse('2018-01-07')
	def NIF='NIF'
	def IBAN='IBAN'
	def IBAN_BUYER='IBAN'
	def car

	@Override
	def populate4Test() {
		RentACar rentACar1=new RentACar(NAME1,NIF,IBAN)
		car=new Car(PLATE_CAR1,10,10,rentACar1)
	}

	def 'success'() {
		given:
        Renting renting = car.rent(DRIVING_LICENSE,date1,date2,NIF,IBAN_BUYER,ADVENTURE_ID)

		when:
		RentingData rentingData = RentACar.getRentingData(renting.getReference())

		then:
		rentingData.getReference() == renting.getReference()
		rentingData.getDrivingLicense() == DRIVING_LICENSE
		PLATE_CAR1.compareToIgnoreCase(rentingData.getPlate()) == 0
		rentingData.getRentACarCode() == this.car.getRentACar().getCode()
	}

	def 'get renting data fail'() {
		when:
        RentACar.getRentingData('1')

		then:
		thrown(CarException)
	}
}
