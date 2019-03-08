package pt.ulisboa.tecnico.softeng.broker.domain;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.*;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

@RunWith(JMockit.class)
public class RentVehicleStateMethodTest extends RollbackTestAbstractClass {
    @Mocked
    private CarInterface carInterface;
    @Mocked
    private TaxInterface taxInterface;

    private RestRentingData rentingData;

    @Override
    public void populate4Test() {
        this.broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN, new ActivityInterface(), new HotelInterface(), this.carInterface, new BankInterface(), this.taxInterface);
        this.client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE);
        this.adventure = new Adventure(this.broker, this.BEGIN, this.END, this.client, MARGIN);

        this.rentingData = new RestRentingData();
        this.rentingData.setReference(RENTING_CONFIRMATION);
        this.rentingData.setPrice(76.78);

        this.adventure.setState(State.RENT_VEHICLE);
    }

    @Test
    public void successRentVehicle() {
        new Expectations() {
            {
                RentVehicleStateMethodTest.this.carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                        RentVehicleStateMethodTest.this.BEGIN, RentVehicleStateMethodTest.this.END, this.anyString);
                this.result = RentVehicleStateMethodTest.this.rentingData;
                this.times = 1;
            }
        };

        this.adventure.process();

        Assert.assertEquals(State.PROCESS_PAYMENT, this.adventure.getState().getValue());
    }

    @Test
    public void carException() {
        new Expectations() {
            {
                RentVehicleStateMethodTest.this.carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                        RentVehicleStateMethodTest.this.BEGIN, RentVehicleStateMethodTest.this.END, this.anyString);
                this.result = new CarException();
                this.times = 1;
            }
        };

        this.adventure.process();

        Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
    }

    @Test
    public void singleRemoteAccessException() {
        new Expectations() {
            {
                RentVehicleStateMethodTest.this.carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                        RentVehicleStateMethodTest.this.BEGIN, RentVehicleStateMethodTest.this.END, this.anyString);
                this.result = new RemoteAccessException();
                this.times = 1;
            }
        };

        this.adventure.process();

        Assert.assertEquals(State.RENT_VEHICLE, this.adventure.getState().getValue());
    }

    @Test
    public void maxRemoteAccessException() {
        new Expectations() {
            {
                RentVehicleStateMethodTest.this.carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                        RentVehicleStateMethodTest.this.BEGIN, RentVehicleStateMethodTest.this.END, this.anyString);
                this.result = new RemoteAccessException();
                this.times = RentVehicleState.MAX_REMOTE_ERRORS;
            }
        };

        for (int i = 0; i < RentVehicleState.MAX_REMOTE_ERRORS; i++) {
            this.adventure.process();
        }

        Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
    }

    @Test
    public void maxMinusOneRemoteAccessException() {
        new Expectations() {
            {
                RentVehicleStateMethodTest.this.carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                        RentVehicleStateMethodTest.this.BEGIN, RentVehicleStateMethodTest.this.END, this.anyString);
                this.result = new RemoteAccessException();
                this.times = RentVehicleState.MAX_REMOTE_ERRORS - 1;
            }
        };

        for (int i = 0; i < RentVehicleState.MAX_REMOTE_ERRORS - 1; i++) {
            this.adventure.process();
        }

        Assert.assertEquals(State.RENT_VEHICLE, this.adventure.getState().getValue());
    }

    @Test
    public void twoRemoteAccessExceptionOneSuccess() {
        new Expectations() {
            {
                RentVehicleStateMethodTest.this.carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                        RentVehicleStateMethodTest.this.BEGIN, RentVehicleStateMethodTest.this.END, this.anyString);
                this.result = new Delegate() {
                    int i = 0;

                    public RestRentingData delegate() {
                        if (this.i < 2) {
                            this.i++;
                            throw new RemoteAccessException();
                        } else {
                            return RentVehicleStateMethodTest.this.rentingData;
                        }
                    }
                };
                this.times = 3;
            }
        };

        this.adventure.process();
        this.adventure.process();
        this.adventure.process();

        Assert.assertEquals(State.PROCESS_PAYMENT, this.adventure.getState().getValue());
    }

    @Test
    public void oneRemoteAccessExceptionOneCarException() {
        new Expectations() {
            {
                RentVehicleStateMethodTest.this.carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                        RentVehicleStateMethodTest.this.BEGIN, RentVehicleStateMethodTest.this.END, this.anyString);
                this.result = new Delegate() {
                    int i = 0;

                    public String delegate() {
                        if (this.i < 1) {
                            this.i++;
                            throw new RemoteAccessException();
                        } else {
                            throw new CarException();
                        }
                    }
                };
                this.times = 2;
            }
        };

        this.adventure.process();
        this.adventure.process();

        Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
    }

}