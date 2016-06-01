
public class BackwardGenerator {
	private double T[][] = {{0.7, 0.3}, {0.3, 0.7}}; //transition prob
	private double B[][] = {{0.9, 0.1}, {0.2, 0.8}}; //emission prob
	
	private int input[] = {0, 0, 1, 0, 0}; //observations
	
	private double b_i[] = {1, 1}; //start vector
	
	private double helper[] = {0, 0};
	private double output[] = {0, 0};
	
	private double norm;
	
	private void computeProbabilities(){
		
		for(int i = input.length-1; i >= 0; i--){
			
			int seq_in = input[i];
			
			for(int j = 0; j < B[0].length; j++){
				helper[j] = b_i[j] * B[j][seq_in]; 
			}
			
			for(int n = 0; n < output.length; n++){
				output[n] = 0;
			}
			
			for(int m = 0; m < T[0].length; m++){
				for(int n = 0; n < T[0].length; n++){
					output[m] += T[m][n] * helper[n]; 
				}	
			}
			
			/*normalize output values to equal 1*/
			norm = 0;
			for(int n = 0; n < output.length; n++){
				norm += output[n];
			}
			
			for(int n = 0; n < output.length; n++){
				output[n] /= norm;
				System.out.println(output[n]);
				b_i[n] = output[n];
			}
			
		}

		
	}
	
	
	public static void main(String[] args) {

		BackwardGenerator backward = new BackwardGenerator();
		backward.computeProbabilities();
	}
}
