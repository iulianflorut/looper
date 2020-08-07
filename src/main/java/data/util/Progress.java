package data.util;

public class Progress extends Thread {

	private boolean running;

	@Override
	public synchronized void start() {
		running = true;
		super.start();
	}

	@Override
	public void run() {
		System.out.print("[");
		var i = 0;
		while (running) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
			if (i++ % 100 == 0)
				System.out.print(".");
		}
		System.out.println("]");
		super.run();
	}

	public void shutbown() {
		this.running = false;
		if (this.isAlive()) {
			try {
				this.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
