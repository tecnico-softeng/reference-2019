package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate
import pt.ist.fenixframework.FenixFramework


class TaxPersistenceSpockTest extends SpockPersistenceTestAbstractClass {
	private static final String SELLER_NIF = '123456789'
	private static final String BUYER_NIF = '987654321'
	private static final String FOOD = 'FOOD'
	private static final int VALUE = 16
	private final LocalDate date = new LocalDate(2018,02,13)

	@Override
	def whenCreateInDatabase() {
		IRS irs=IRS.getIRSInstance()
		Seller seller=new Seller(irs, SELLER_NIF,'José Vendido','Somewhere')
		Buyer buyer=new Buyer(irs, BUYER_NIF,'Manuel Comprado','Anywhere')
		ItemType it=new ItemType(irs, FOOD,VALUE)
		new Invoice(VALUE, date, it, seller, buyer)
	}


	def 'success'() {
		when:
		atomicProcess()

		then:
		atomicAssert()

	}


	@Override
	def thenAssert() {
		IRS irs=IRS.getIRSInstance()
		assert 2 == irs.getTaxPayerSet().size()

		TaxPayer taxPayer1=new ArrayList<>(irs.getTaxPayerSet()).get(0)
		if (taxPayer1 instanceof Seller) {
			assert SELLER_NIF == taxPayer1.getNif()
		} else {
			assert  BUYER_NIF == taxPayer1.getNif()
		}

		TaxPayer taxPayer2=new ArrayList<>(irs.getTaxPayerSet()).get(1)
		if (taxPayer2 instanceof Seller) {
			assert SELLER_NIF == taxPayer2.getNif()
		} else {
			assert BUYER_NIF == taxPayer2.getNif()
		}

		assert 1 == irs.getItemTypeSet().size()

		ItemType itemType=new ArrayList<>(irs.getItemTypeSet()).get(0)
		assert VALUE == itemType.getTax()
		assert FOOD == itemType.getName()
		assert 1 == irs.getInvoiceSet().size()

		Invoice invoice=new ArrayList<>(irs.getInvoiceSet()).get(0)
		assert VALUE == invoice.getValue()
		assert invoice.getReference() != null
		assert date == invoice.getDate()
		assert BUYER_NIF == invoice.getBuyer().getNif()
		assert SELLER_NIF == invoice.getSeller().getNif()
		assert itemType == invoice.getItemType()
		assert invoice.getTime() != null
		assert !invoice.getCancelled()
	}

	@Override
	def deleteFromDatabase() {
		FenixFramework.getDomainRoot().getIrs().delete()
	}
}
