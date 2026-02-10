package core.communication;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

import core.scheduling.Statistics;

/**
 * 
 * Classe que representa uma mensagem que será trocada pelas localidades. Cada
 * mensagem terá uma etiqueta associada, semelhante ao usado no MPI com o
 * atributo TAG.
 * 
 * @author Diogo Mourão
 * @author João Saramago
 * 
 * @param <C> Tipo de dados a enviar
 */
public class Message<C> implements Externalizable {

	private static final long serialVersionUID = 5022613830834967768L;

	/*
	 * Não esquecer que todos os atributos da classe terão que ser Serializable
	 */
	private UUID destination_object;
	@SuppressWarnings("rawtypes")
	private Node destination_host;
	private UUID sending_object;
	@SuppressWarnings("rawtypes")
	private Node sending_host;
	private MessageTag tag;
	private C content;

	private Statistics sta;

	
	/**
	 * Construtor de uma mensagem. 
	 * @param type Tipo de mensagem
	 * @param content Conteudo
	 * @param sendingobject Identificador do objecto que envia 
	 */
	public Message(MessageTag type, C content, UUID sendingobject) {
		// TODO - Message
		this.tag = type;
		this.content = content;
		this.sending_object = sendingobject;
	}

	/**
	 * Construtor de uma mensagem. 
	 * @param type Tipo de mensagem
	 * @param content Conteudo
	 * @param sendingobject Identificador do objecto que envia 
	 * @param host Nó destino
	 */
	@SuppressWarnings("rawtypes")
	public Message(MessageTag type, C content, UUID sendingobject, Node host) {
		this(type, content, sendingobject);
		this.destination_host = host;
	}

	/**
	 * Retorna o identificador do objecto que enviou a mensagem
	 * @return Identificador do objecto que enviou a mensagem
	 */
	public UUID getSendingObject() {
		return this.sending_object;
	}

	/**
	 * 
	 * Método que retorna a etiqueta da mensagem.
	 * 
	 * @return Tag da mensagem.
	 */
	public MessageTag getType() {
		return tag;
	}

	/**
	 * 
	 * Método que retorna o conteúdo da mensagem.
	 * 
	 * @return Conteúdo da mensagem.
	 */
	public C getContent() {
		return content;
	}

	/**
	 * 
	 * Método que retorna o identificador da localidade destinatário da
	 * mensagem.
	 * 
	 * @return Identificador da localidade destino da mensagem.
	 */
	public UUID getDestination_object() {
		return destination_object;
	}

	/**
	 * Método onde se define o identificador do objecto que irá receber a mensagem
	 * @param destination_place Identificador do objecto que irá receber a mensagem
	 */
	public void setDestination_object(UUID destination_object) {
		this.destination_object = destination_object;
	}

	/**
	 * Método que retorna o identificador do nó que irá receber a mensagem
	 * @return Identificador do nó que irá receber a mensagem
	 */
	@SuppressWarnings("rawtypes")
	public Node getDestination_host() {
		return destination_host;
	}

	/**
	 * Método onde se define o identificador do nó destino
	 * @param destination_host Identificador do nó destino
	 */
	@SuppressWarnings("rawtypes")
	public void setDestination_host(Node destination_host) {
		this.destination_host = destination_host;
	}

	/**
	 * Método onde se define o identificador do nó que enviou a mensagem
	 * @param client Identificador do nó que enviou a mensagem
	 */
	@SuppressWarnings("rawtypes")
	public void setSending_Host(Node client) {
		this.sending_host = client;
	}

	/**
	 * Método que retorna o identificador do nó que enviou a mensagem
	 * @return Identificador do nó que enviou a mensagem
	 */
	@SuppressWarnings("rawtypes")
	public Node getSending_host() {
		return sending_host;
	}

	/**
	 * Método que retorna as estatisticas do nó quen enviou a mensagem
	 * @return Estatisticas do nó quen enviou a mensagem
	 */
	public Statistics getStatistics() {
		return sta;
	}

	/**
	 * Método onde se define as estatisticas do nó quen enviou a mensagem
	 * @param s Estatisticas do nó quen enviou a mensagem
	 */
	public void setStatistics(Statistics s) {
		this.sta = s;
	}

	/**
	 * Método que define como a mensagem é serializada
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		if(destination_object==null)
			out.writeUTF("");
		else{
			out.writeUTF(destination_object.toString());
		}
		out.writeObject(destination_host);
		if(sending_object==null)
			out.writeUTF("");
		else{
			out.writeUTF(sending_object.toString());
		}
		out.writeObject(sending_host);
		out.writeInt(tag.ordinal());
		out.writeObject(content);
		out.writeObject(sta);

	}

	/**
	 * Método que define como a mensagem é deserializada
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		String aux;
		
		aux=in.readUTF();
		if(!aux.equals("")){
			destination_object = UUID.fromString(aux);
		}
		
		
		destination_host = (Node<?>) in.readObject();
		
		aux=in.readUTF();
		if(!aux.equals("")){
			sending_object = UUID.fromString(aux);
		}
		
		sending_host = (Node<?>) in.readObject();
		tag = MessageTag.values()[in.readInt()];
		content = (C) in.readObject();
		sta = (Statistics) in.readObject();
	}

}
