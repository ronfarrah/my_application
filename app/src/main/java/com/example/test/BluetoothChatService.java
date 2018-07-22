package com.example.test;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothChatService {
	private static final boolean D = true;
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final String NAME = "BluetoothCom";
	public static final int STATE_CONNECTED = 3;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_LISTEN = 1;
	public static final int STATE_NONE = 0;
	private static final String TAG = "BluetoothChatService";
	private AcceptThread mAcceptThread;
	private final BluetoothAdapter mAdapter = BluetoothAdapter
			.getDefaultAdapter();
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private final Handler mHandler;
	private int mState = 0;

	private BluetoothServerSocket localBluetoothServerSocket1;
	private BluetoothSocket localBluetoothSocket1;
	private OutputStream localOutputStream1;

	public BluetoothChatService(Context paramContext, Handler paramHandler) {
		this.mHandler = paramHandler;
	}

	private void connectionFailed() {
		setState(1);
		Message localMessage = this.mHandler.obtainMessage(5);
		Bundle localBundle = new Bundle();
		localBundle.putString("toast", "连接失败");
		localMessage.setData(localBundle);
		this.mHandler.sendMessage(localMessage);
	}

	private void connectionLost() {
		setState(1);
		Message localMessage = this.mHandler.obtainMessage(5);
		Bundle localBundle = new Bundle();
		localBundle.putString("toast", "蓝牙已断开连接");
		localMessage.setData(localBundle);
		this.mHandler.sendMessage(localMessage);
	}

	private void setState(int paramInt) {
		try {
			Log.d("BluetoothChatService", "setState() " + this.mState + " -> "
					+ paramInt);
			this.mState = paramInt;
			this.mHandler.obtainMessage(1, paramInt, -1).sendToTarget();
			return;
		} finally {
			// localObject = finally;
			// throw localObject;
		}
	}


	public void connect(BluetoothDevice paramBluetoothDevice) {
		try {
			Log.d("BluetoothChatService", "connect to: " + paramBluetoothDevice);
			if ((this.mState == 2) && (this.mConnectThread != null)) {
				this.mConnectThread.cancel();
				this.mConnectThread = null;
			}
			if (this.mConnectedThread != null) {
				this.mConnectedThread.cancel();
				this.mConnectedThread = null;
			}
			this.mConnectThread = new ConnectThread(paramBluetoothDevice);
			this.mConnectThread.start();
			setState(2);
			return;
		} finally {
		}
	}

	public void connected(BluetoothSocket paramBluetoothSocket,
						  BluetoothDevice paramBluetoothDevice) {
		try {
			Log.d("BluetoothChatService", "connected");
			if (this.mConnectThread != null) {
				this.mConnectThread.cancel();
				this.mConnectThread = null;
			}
			if (this.mConnectedThread != null) {
				this.mConnectedThread.cancel();
				this.mConnectedThread = null;
			}
			if (this.mAcceptThread != null) {
				this.mAcceptThread.cancel();
				this.mAcceptThread = null;
			}
			this.mConnectedThread = new ConnectedThread(paramBluetoothSocket);
			this.mConnectedThread.start();
			Message localMessage = this.mHandler.obtainMessage(4);
			Bundle localBundle = new Bundle();
			localBundle
					.putString("device_name", paramBluetoothDevice.getName());
			localMessage.setData(localBundle);
			this.mHandler.sendMessage(localMessage);
			setState(3);
			return;
		} finally {
		}
	}

	public int getState() {
		try {
			int i = this.mState;
			return i;
		} finally {
			// localObject = finally;
			// throw localObject;
		}
	}

	public void start() {
		try {
			Log.d("BluetoothChatService", "start");
			if (this.mConnectThread != null) {
				this.mConnectThread.cancel();
				this.mConnectThread = null;
			}
			if (this.mConnectedThread != null) {
				this.mConnectedThread.cancel();
				this.mConnectedThread = null;
			}
			if (this.mAcceptThread == null) {
				this.mAcceptThread = new AcceptThread();
				this.mAcceptThread.start();
			}
			setState(1);
			return;
		} finally {
		}
	}

	public void stop() {
		try {
			Log.d("BluetoothChatService", "stop");
			if (this.mConnectThread != null) {
				this.mConnectThread.cancel();
				this.mConnectThread = null;
			}
			if (this.mConnectedThread != null) {
				this.mConnectedThread.cancel();
				this.mConnectedThread = null;
			}
			if (this.mAcceptThread != null) {
				this.mAcceptThread.cancel();
				this.mAcceptThread = null;
			}
			setState(0);
			return;
		} finally {
		}
	}

	public void write(byte[] paramArrayOfByte) {
		try {
			if (this.mState != 3)
				return;
			ConnectedThread localConnectedThread = this.mConnectedThread;
			localConnectedThread.write(paramArrayOfByte);
			return;
		} finally {
		}
	}

	private class AcceptThread extends Thread {
		private final BluetoothServerSocket mmServerSocket;

		public AcceptThread() {
			try {
				BluetoothServerSocket localBluetoothServerSocket2 = BluetoothChatService.this.mAdapter
						.listenUsingRfcommWithServiceRecord("BluetoothCom",
								BluetoothChatService.MY_UUID);
				localBluetoothServerSocket1 = localBluetoothServerSocket2;
				this.mmServerSocket = localBluetoothServerSocket1;
				return;
			} catch (IOException localIOException) {
				while (true) {
					Log.e("BluetoothChatService", "listen() failed",
							localIOException);
					BluetoothServerSocket localBluetoothServerSocket1 = null;
				}
			}
		}

		public void cancel() {
			Log.d("BluetoothChatService", "cancel " + this);
			try {
				this.mmServerSocket.close();
				return;
			} catch (IOException localIOException) {
				Log.e("BluetoothChatService", "close() of server failed",
						localIOException);
			}
		}

		public void run() {
			Log.d("BluetoothChatService", "BEGIN mAcceptThread" + this);
			setName("AcceptThread");
			if (BluetoothChatService.this.mState == 3) {
				Log.i("BluetoothChatService", "END mAcceptThread");
				return;
			}
			while (true) {
				BluetoothSocket localBluetoothSocket = null;
				try {
					while (true) {
						localBluetoothSocket = this.mmServerSocket.accept();
						if (localBluetoothSocket == null)
							break;
						synchronized (BluetoothChatService.this) {
							switch (BluetoothChatService.this.mState) {
								default:
								case 1:
								case 2:
								case 0:
								case 3:
							}
						}
					}
				} catch (IOException localIOException1) {
					Log.e("BluetoothChatService", "accept() failed",
							localIOException1);
				}
				BluetoothChatService.this.connected(localBluetoothSocket,
						localBluetoothSocket.getRemoteDevice());
				try {
					localBluetoothSocket.close();
				} catch (IOException localIOException2) {
					Log.e("BluetoothChatService",
							"Could not close unwanted socket",
							localIOException2);
				}
			}
		}
	}

	private class ConnectThread extends Thread {
		private final BluetoothDevice mmDevice;
		private final BluetoothSocket mmSocket;

		public ConnectThread(BluetoothDevice arg2) {
			Object localObject;
			this.mmDevice = arg2;
			try {
				BluetoothSocket localBluetoothSocket2 = arg2
						.createRfcommSocketToServiceRecord(BluetoothChatService.MY_UUID);
				localBluetoothSocket1 = localBluetoothSocket2;
				this.mmSocket = localBluetoothSocket1;
				return;
			} catch (IOException localIOException) {
				while (true) {
					Log.e("BluetoothChatService", "create() failed",
							localIOException);
					BluetoothSocket localBluetoothSocket1 = null;
				}
			}
		}

		public void cancel() {
			try {
				this.mmSocket.close();
				return;
			} catch (IOException localIOException) {
				Log.e("BluetoothChatService",
						"close() of connect socket failed", localIOException);
			}
		}

		public void run() {
			Log.i("BluetoothChatService", "BEGIN mConnectThread");
			setName("ConnectThread");
			BluetoothChatService.this.mAdapter.cancelDiscovery();
			try {
				this.mmSocket.connect();
			} catch (IOException localIOException1) {
				synchronized (BluetoothChatService.this) {
					BluetoothChatService.this.mConnectThread = null;
					BluetoothChatService.this.connected(this.mmSocket,
							this.mmDevice);
					BluetoothChatService.this.connectionFailed();
					try {
						this.mmSocket.close();
						BluetoothChatService.this.start();
						return;
					} catch (IOException localIOException2) {
						while (true)
							Log.e("BluetoothChatService",
									"unable to close() socket during connection failure",
									localIOException2);
					}
				}
			}
		}
	}

	private class ConnectedThread extends Thread {
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;
		private final BluetoothSocket mmSocket;

		public ConnectedThread(BluetoothSocket arg2) {
			Log.d("BluetoothChatService", "create ConnectedThread");
			BluetoothSocket localObject = arg2;
			this.mmSocket = localObject;
			InputStream localInputStream = null;
			try {
				localInputStream = localObject.getInputStream();
				OutputStream localOutputStream2 = localObject.getOutputStream();
				localOutputStream1 = localOutputStream2;
				this.mmInStream = localInputStream;
				this.mmOutStream = localOutputStream1;
				return;
			} catch (IOException localIOException) {
				while (true) {
					Log.e("BluetoothChatService", "temp sockets not created",
							localIOException);
					OutputStream localOutputStream1 = null;
				}
			}
		}

		public void cancel() {
			try {
				this.mmSocket.close();
				return;
			} catch (IOException localIOException) {
				Log.e("BluetoothChatService",
						"close() of connect socket failed", localIOException);
			}
		}

		public void run() {
			Log.i("BluetoothChatService", "BEGIN mConnectedThread");
			byte[] arrayOfByte1 = new byte[1024];
			while (true) {
				int j;
				try {
					int i = this.mmInStream.read(arrayOfByte1);
					if (i <= 0)
						continue;
					byte[] arrayOfByte2 = (byte[]) arrayOfByte1.clone();
					j = 0;
					if (j >= arrayOfByte1.length) {
						BluetoothChatService.this.mHandler.obtainMessage(2, i,
								-1, arrayOfByte2).sendToTarget();
						continue;
					}
				} catch (IOException localIOException) {
					Log.e("BluetoothChatService", "disconnected",
							localIOException);
					BluetoothChatService.this.connectionLost();
					return;
				}
				arrayOfByte1[j] = 0;
				j++;
			}
		}

		public void write(byte[] paramArrayOfByte) {
			try {
				this.mmOutStream.write(paramArrayOfByte);
				BluetoothChatService.this.mHandler.obtainMessage(3, -1, -1,
						paramArrayOfByte).sendToTarget();
				return;
			} catch (IOException localIOException) {
				Log.e("BluetoothChatService", "Exception during write",
						localIOException);
			}
		}
	}
}
