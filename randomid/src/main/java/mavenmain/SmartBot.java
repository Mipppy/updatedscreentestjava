package mavenmain;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SmartBot {
	Thread smartBotThread; //= new Thread(() -> {beingASmartGuy();});
	private static int gameId = 000000;
	private static int aiRequestCount = 0;
	private static int maxAiRequestCount = 10;
	public static boolean stopFlag = false;
	public static WebDriver driver; // = new FirefoxDriver();
	private static void beingASmartGuy() {
		if (handleLogin()) {
			actualBeTheSmartGuy();
		}
		driver.quit();
		stopFlag = false;
	}
	
	private static boolean handleLogin() {
	    driver.navigate().to("https://kahoot.it/?pin=" + gameId);  
    	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    	WebElement gameUsernameElement = null;
    	try {
    		if (stopFlag) {
    			return false;
    		}
        	wait.until(ExpectedConditions.urlContains("join"));
    		gameUsernameElement = wait.until(ExpectedConditions.elementToBeClickable(By.id("nickname")));
    	} catch (TimeoutException e) {
    		return false;
    	}
    	if (gameUsernameElement == null || stopFlag) {
    		return false;
    	}
        gameUsernameElement.sendKeys("Tim " + UUID.randomUUID().toString().substring(0,4));
        WebElement gameUsernameClick = driver.findElement(By.cssSelector(".nickname-form__SubmitButton-sc-1mjq176-1"));
        gameUsernameClick.click();	
        return true;
	}
	
	private static void actualBeTheSmartGuy() {
	    String question = null;

	    while (!stopFlag) {
	        String currentUrl = driver.getCurrentUrl();

	        if (currentUrl.contains("getready")) {
	            try {
	                question = driver.findElement(By.cssSelector("h1[aria-live='assertive']")).getText();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }

	        if (currentUrl.contains("gameblock")) {
	            List<WebElement> answers = driver.findElements(By.cssSelector(".choice__Choice-sc-ym3b8f-4"));
	            List<String> stringAnswers = new ArrayList<>();
	            System.out.println(question);
	            for (WebElement n : answers) {
	                stringAnswers.add(n.getText());
	            }
	            int answerNum = answer(question, stringAnswers);

	            boolean clicked = false;
	            int attempts = 0;
	            while (!clicked || attempts < 3) {
	                try {
	                	try {
	                    answers.get(answerNum - 1).click();
	                	} catch (IndexOutOfBoundsException e) {
		                    attempts++;
		                    answers = driver.findElements(By.cssSelector(".choice__Choice-sc-ym3b8f-4"));
	                	}
	                    clicked = true;
	                } catch (StaleElementReferenceException e) {
	                    attempts++;
	                    answers = driver.findElements(By.cssSelector(".choice__Choice-sc-ym3b8f-4"));
	                }
	            }

	            if (!clicked) {
	                System.out.println("Failed to click after multiple attempts.");
	            }
	        }
	    }
	    driver.quit();
	}

	
	
	 private static int answer(String question, List<String> answers) {
	        String url = "https://dev-api.computer.com/api/questions/546990/stream/";
	        String data = "e";
	        System.out.println(answers);
	        try {
	            StringBuilder dataBuilder = new StringBuilder(question);
	            for (int i = 0; i < answers.size(); i++) {
	                dataBuilder.append((i + 1) + "." + answers.get(i));
	            }
	            dataBuilder.append("           ANSWER WITH ONLY THE NUMBER OF THE CORRECT QUESTION!!");
	            data = dataBuilder.toString();
	        } catch (IndexOutOfBoundsException e) {
	            e.printStackTrace();
	        }
	        String requestBody = "{\"question\":\"" + data + "\"}";
	        System.out.println(requestBody);
	        int retries = 0;
	        while (retries < maxAiRequestCount) {
	            try {
	                URL obj = new URL(url);
	                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	                con.setRequestMethod("POST");
	                con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:126.0) Gecko/20100101 Firefox/126.0");
	                con.setRequestProperty("Accept", "*/*");
	                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	                con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
	                con.setRequestProperty("Sec-GPC", "1");
	                con.setRequestProperty("Sec-Fetch-Dest", "empty");
	                con.setRequestProperty("Sec-Fetch-Mode", "cors");
	                con.setRequestProperty("Sec-Fetch-Site", "same-site");
	                con.setRequestProperty("Priority", "u=4");
	                con.setRequestProperty("Access-Control-Allow-Origin", "*");
	                con.setDoOutput(true);

	                try (OutputStream os = con.getOutputStream()) {
	                    byte[] input = requestBody.getBytes("utf-8");
	                    os.write(input, 0, input.length);
	                }

	                int responseCode = con.getResponseCode();
	                System.out.println("Response Code: " + responseCode);

	                if (responseCode == HttpURLConnection.HTTP_OK) {
	                    try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
	                        String inputLine;
	                        StringBuilder response = new StringBuilder();
	                        while ((inputLine = in.readLine()) != null) {
	                            response.append(inputLine);
	                        }
	                        List<String> strings = extractStrings(response.toString());
	                        StringBuilder result = new StringBuilder();
	                        for (String str : strings) {
	                            result.append(str);
	                        }
	                        System.out.println("Result: " + result);
	                        Pattern pattern = Pattern.compile("\\d+");
	                        Matcher matcher = pattern.matcher(result);
	                        if (matcher.find()) {
	                            String numberStr = matcher.group();
	                            int number = Integer.parseInt(numberStr);
	                            return number;
	                        } else {
	                            System.out.println("No number found in the string.");
	                            return 1;
	                        }
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
	                } else {
	                    System.out.println("HTTP Error: " + responseCode);
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            }

	            retries++;
	            if (retries < maxAiRequestCount) {
	                System.out.println("Retrying... Attempt: " + retries);
	                try {
	                    Thread.sleep(1000); // Wait for 1 second before retrying
	                } catch (InterruptedException ex) {
	                    Thread.currentThread().interrupt();
	                }
	            }
	        }

	        return 1;
	    }
	
	public SmartBot(int game) {
		gameId = game;
	        gameId = game;
	        driver = new FirefoxDriver(); // Initialize WebDriver in the constructor
	        smartBotThread = new Thread(()-> {SmartBot.beingASmartGuy();}); // Moved thread creation to constructor
	    
	}
	
	
    private static List<String> extractStrings(String jsonString) {
        List<String> strings = new ArrayList<>();
        boolean insideQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (char c : jsonString.toCharArray()) {
            if (c == '"') {
                insideQuotes = !insideQuotes; 
            } else if (insideQuotes) {
                sb.append(c); 
            } else if (sb.length() > 0) {
                strings.add(sb.toString()); 
                sb.setLength(0);
            }
        }
        for (int i = strings.size() - 1; i >= 0; i--) {
            if (i % 2 != 0) {
                strings.remove(i);
            }
        }
        return strings;
    }
}
