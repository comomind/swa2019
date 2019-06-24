package com.lg.sixsenses.willi.net;

import java.util.Arrays;

public class RtpPacket {

  /**
   *
   * It covers the RTP header fields until where the variability starts (the CSRCs list)
   *
   *
   * https://tools.ietf.org/html/rfc3550#section-5.1
   *  0                   1                   2                   3
   *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   * |V=2|P|X|  CC   |M|     PT      |       sequence number         |
   * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   * |                           timestamp                           |
   * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   * |           synchronization source (SSRC) identifier            |
   * +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
   * |            contributing source (CSRC) identifiers             |
   * |                             ....                              |
   * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   * |              ...extensions (if present)...                    |
   * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   *
   */

  //size of the RTP header:
  public static int HEADER_SIZE = 12;

  //Fields that compose the RTP header
  public int Version;
  public int Padding;
  public int Extension;
  public int CC;
  public int Marker;
  public int PayloadType;
  public int SequenceNumber;
  public int TimeStamp;
  public int Ssrc;

  //Bitstream of the RTP header
  public byte[] header;

  //size of the RTP payload
  public int payload_size;
  //Bitstream of the RTP payload
  public byte[] payload;

  //--------------------------
  //Constructor of an RtpPacket object from header fields and payload bitstream
  //--------------------------
  public RtpPacket(int PType, int Framenb, int Time, byte[] data, int data_length){
    //fill by default header fields:
    Version = 2;
    Padding = 0;
    Extension = 0;
    CC = 0;
    Marker = 0;
    Ssrc = 1337;    // TODO: Identifies the server

    //fill changing header fields:
    SequenceNumber = Framenb;
    TimeStamp = Time;
    PayloadType = PType;

    //build the header bistream:
    header = new byte[HEADER_SIZE];

    //fill the header array of byte with RTP header fields
    header[0] = (byte)(Version << 6 | Padding << 5 | Extension << 4 | CC);
    header[1] = (byte)(Marker << 7 | PayloadType & 0x000000FF);
    header[2] = (byte)(SequenceNumber >> 8);
    header[3] = (byte)(SequenceNumber & 0xFF);
    header[4] = (byte)(TimeStamp >> 24);
    header[5] = (byte)(TimeStamp >> 16);
    header[6] = (byte)(TimeStamp >> 8);
    header[7] = (byte)(TimeStamp & 0xFF);
    header[8] = (byte)(Ssrc >> 24);
    header[9] = (byte)(Ssrc >> 16);
    header[10] = (byte)(Ssrc >> 8);
    header[11] = (byte)(Ssrc & 0xFF);

    //fill the payload bitstream:
    payload_size = data_length;
    payload = new byte[data_length];

    //fill payload array of byte from data (given in parameter of the constructor)
    payload = Arrays.copyOf(data, payload_size);
  }

  public RtpPacket(byte[] packet, int packet_size)
  {
    //fill default fields:
    Version = 2;
    Padding = 0;
    Extension = 0;
    CC = 0;
    Marker = 0;
    Ssrc = 0;

    //check if total packet size is lower than the header size
    if (packet_size >= HEADER_SIZE)
    {
      //get the header bit stream
      header = new byte[HEADER_SIZE];
      for (int i=0; i < HEADER_SIZE; i++)
        header[i] = packet[i];

      //get the payload bit stream:
      payload_size = packet_size - HEADER_SIZE;
      payload = new byte[payload_size];
      for (int i=HEADER_SIZE; i < packet_size; i++)
        payload[i-HEADER_SIZE] = packet[i];

      //interpret the changing fields of the header:
      Version = (header[0] & 0xFF) >>> 6;
      PayloadType = header[1] & 0x7F;
      SequenceNumber = (header[3] & 0xFF) + ((header[2] & 0xFF) << 8);
      TimeStamp = (header[7] & 0xFF) + ((header[6] & 0xFF) << 8) + ((header[5] & 0xFF) << 16) + ((header[4] & 0xFF) << 24);
    }
  }

  public int getPayload(byte[] data) {

    for (int i=0; i < payload_size; i++)
      data[i] = payload[i];

    return(payload_size);
  }

  public int getPayloadLength() {
    return(payload_size);
  }

  public int getLength() {
    return(payload_size + HEADER_SIZE);
  }

  public int getPacket(byte[] packet)
  {
    //construct the packet = header + payload
    for (int i=0; i < HEADER_SIZE; i++)
      packet[i] = header[i];
    for (int i=0; i < payload_size; i++)
      packet[i+HEADER_SIZE] = payload[i];

    //return total size of the packet
    return(payload_size + HEADER_SIZE);
  }

  public int getTimestamp() {
    return(TimeStamp);
  }

  public int getSequenceNumber() {
    return(SequenceNumber);
  }

  public int getPayloadType() {
    return(PayloadType);
  }

  public void printHeader()
  {
    System.out.print("[RTP-Header] ");
    System.out.println("Version: " + Version
        + ", Padding: " + Padding
        + ", Extension: " + Extension
        + ", CC: " + CC
        + ", Marker: " + Marker
        + ", PayloadType: " + PayloadType
        + ", SequenceNumber: " + SequenceNumber
        + ", TimeStamp: " + TimeStamp);
  }
}