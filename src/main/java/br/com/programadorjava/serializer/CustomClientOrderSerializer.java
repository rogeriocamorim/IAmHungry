package br.com.programadorjava.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import br.com.programadorjava.entity.ClientOrder;
import br.com.programadorjava.entity.OrderItem;

public class CustomClientOrderSerializer extends StdSerializer<ClientOrder> {

	public CustomClientOrderSerializer() {
		this(null);
	}

	public CustomClientOrderSerializer(Class<ClientOrder> t) {
		super(t);
	}

	@Override
	public void serialize(ClientOrder clientOrder, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		jsonGenerator.writeStartObject();
		jsonGenerator.writeStringField("Order Id", String.valueOf(clientOrder.getId()));
		jsonGenerator.writeStringField("Customer", clientOrder.getCustomer().getName());
		jsonGenerator.writeStringField("deliveryAddress", clientOrder.getDeliveryAddress());
		jsonGenerator.writeStringField("contact", clientOrder.getContact());		
		jsonGenerator.writeStringField("total", String.valueOf(clientOrder.getTotal()));
		jsonGenerator.writeStringField("status", clientOrder.getStatus());

		jsonGenerator.writeEndObject();

	}

}
