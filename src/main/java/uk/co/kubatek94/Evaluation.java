package uk.co.kubatek94;

import uk.co.kubatek94.dataset.Dataset;
import uk.co.kubatek94.dataset.Facebook;
import uk.co.kubatek94.dataset.Gplus;
import uk.co.kubatek94.dataset.Twitter;
import uk.co.kubatek94.util.EvaluationRunnable;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by kubatek94 on 04/06/16.
 */
public class Evaluation {
	public static void main(String[] args) {
		Supplier<Dataset> gplusSupplier = () -> new Gplus();
		Supplier<Dataset> facebookSupplier = () -> new Facebook();
		Supplier<Dataset> twitterSupplier = () -> new Twitter();

		//execute for 2, 4, 8, 16 partitions
		for (int power = 1, i = 2; power <= 4; power++, i = (int) Math.pow(2, power)) {
			System.out.println("Evaluating facebook with " + i + " partitions");
			new EvaluationRunnable(Stream.generate(facebookSupplier).limit(8), i, 13).run(); //8 threads * 13 repetitions -> 104 repetitions each
		}

		//execute for 2, 4, 8, 16 partitions
		for (int power = 1, i = 2; power <= 4; power++, i = (int) Math.pow(2, power)) {
			System.out.println("Evaluating twitter with " + i + " partitions");
			new EvaluationRunnable(Stream.generate(twitterSupplier).limit(8), i, 13).run(); //8 threads * 13 repetitions -> 104 repetitions each
		}

		//execute for 2, 4, 8, 16 partitions
		for (int power = 1, i = 2; power <= 4; power++, i = (int) Math.pow(2, power)) {
			System.out.println("Evaluating gplus with " + i + " partitions");
			new EvaluationRunnable(Stream.generate(gplusSupplier).limit(4), i, 25).run(); //4 threads * 25 repetitions -> 100 repetitions each
		}
	}
}
