package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

public class Controller {

    private Stage stage;
    private Stack<Shape> undoHistory = new Stack<>();
    private Stack<Shape> redoHistory = new Stack<>();

    private GraphicsContext graphicsContext2D;

    private Line line = new Line();
    private Rectangle rect = new Rectangle();
    private Circle circle = new Circle();
    private Ellipse ellipse = new Ellipse();

    @FXML
    private MenuItem mItemCoordinates;

    @FXML
    private MenuItem mItemScale;


    @FXML
    private MenuItem menuItemRect;

    @FXML
    private MenuItem menuItemCircle;

    @FXML
    private MenuItem menuItemEllipse;


    @FXML
    private MenuItem mItemColorRed;

    @FXML
    private MenuItem mItemColorGreen;

    @FXML
    private MenuItem mItemColorBlue;

    @FXML
    private ToggleButton drowBtn;

    @FXML
    private ToggleGroup tools;

    @FXML
    private ToggleButton lineBtn;

    @FXML
    private ToggleButton rectangleBtn;

    @FXML
    private ToggleButton circleBtn;

    @FXML
    private ToggleButton ellipseBtn;

    @FXML
    private ToggleButton rubberBtn;

    @FXML
    private ColorPicker lineCP;

    @FXML
    private ColorPicker pouringCP;

    @FXML
    private Label labelSliderLine;

    @FXML
    private Slider sliderLine;

    @FXML
    private Button untoBtn;

    @FXML
    private Button redoBtn;

    @FXML
    private Button saveBtn;

    @FXML
    private Canvas canvas;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        canvas.setHeight(530);
        canvas.setWidth(650);

        lineCP.setValue(Color.BLACK);
        pouringCP.setValue(Color.TRANSPARENT);

        graphicsContext2D = canvas.getGraphicsContext2D();
        graphicsContext2D.setLineWidth(sliderLine.getValue());

        sliderLine.valueProperty().addListener(e -> {
            double width = sliderLine.getValue();
            System.out.println(width);
            labelSliderLine.setText(String.format("%.1f", width));
            graphicsContext2D.setLineWidth(width);
        });

        menuItemRect.setOnAction(e -> rectangleBtn.setSelected(true));

        menuItemCircle.setOnAction(event -> circleBtn.setSelected(true));

        menuItemEllipse.setOnAction(event -> ellipseBtn.setSelected(true));

        mItemColorRed.setOnAction(event -> pouringCP.setValue(Color.RED));

        mItemColorBlue.setOnAction(event -> pouringCP.setValue(Color.BLUE));

        mItemColorGreen.setOnAction(event -> pouringCP.setValue(Color.GREEN));
    }

    @FXML
    void canvasMouseDragged(MouseEvent event) {
        if (drowBtn.isSelected()) {
            graphicsContext2D.lineTo(event.getX(), event.getY());
            graphicsContext2D.stroke();
        } else if (rubberBtn.isSelected()) {
            double lineWidth = graphicsContext2D.getLineWidth();
            graphicsContext2D.clearRect(event.getX() - lineWidth / 2, event.getY() - lineWidth / 2, lineWidth, lineWidth);
        }
    }

    @FXML
    void canvasMousePressed(MouseEvent event) {
        graphicsContext2D.setStroke(lineCP.getValue());
        if (drowBtn.isSelected()) {
            System.out.println("drawBtn selected");

            graphicsContext2D.beginPath();
            graphicsContext2D.lineTo(event.getX(), event.getY());
        } else if (rubberBtn.isSelected()) {
            double lineWidth = graphicsContext2D.getLineWidth();
            graphicsContext2D.clearRect(event.getX() - lineWidth / 2, event.getY() - lineWidth / 2, lineWidth, lineWidth);
        } else if (lineBtn.isSelected()) {

            line.setStartX(event.getX());
            line.setStartY(event.getY());
        } else if (rectangleBtn.isSelected()) {

            graphicsContext2D.setFill(pouringCP.getValue());
            rect.setX(event.getX());
            rect.setY(event.getY());
        } else if (circleBtn.isSelected()) {

            graphicsContext2D.setFill(pouringCP.getValue());
            circle.setCenterX(event.getX());
            circle.setCenterY(event.getY());
        } else if (ellipseBtn.isSelected()) {

            graphicsContext2D.setFill(pouringCP.getValue());
            ellipse.setCenterX(event.getX());
            ellipse.setCenterY(event.getY());
        }
    }

    @FXML
    void canvasMouseReleased(MouseEvent event) {
        if (drowBtn.isSelected()) {
            graphicsContext2D.lineTo(event.getX(), event.getY());
            graphicsContext2D.stroke();
            graphicsContext2D.closePath();
        } else if (rubberBtn.isSelected()) {
            double lineWidth = graphicsContext2D.getLineWidth();
            graphicsContext2D.clearRect(event.getX() - lineWidth / 2, event.getY() - lineWidth / 2, lineWidth, lineWidth);
        } else if (lineBtn.isSelected()) {
            line.setEndX(event.getX());
            line.setEndY(event.getY());
            graphicsContext2D.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());

            //Тут стек возврата!
        } else if (rectangleBtn.isSelected()) {
            rect.setWidth(Math.abs(event.getX() - rect.getX()));
            rect.setHeight(Math.abs(event.getY() - rect.getY()));

           /* if (rect.getX() > event.getX())
                rect.setX(event.getY());

            if (rect.getY() > event.getY())
                rect.setY(event.getY());*/

            rect.setX((rect.getX() > event.getX()) ? event.getX() : rect.getX());
            rect.setY((rect.getY() > event.getY()) ? event.getY() : rect.getY());
            graphicsContext2D.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
            graphicsContext2D.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

            //Тут стек возврата!
        } else if (circleBtn.isSelected()) {
            circle.setRadius((Math.abs(event.getX() - circle.getCenterX()) + Math.abs(event.getY() - circle.getCenterY())) / 2);
            /*
            circle.setCenterX((circle.getCenterX() > event.getX()) ? event.getX() : circle.getCenterX());
            circle.setCenterY((circle.getCenterY() > event.getY()) ? event.getY() : circle.getCenterY());
*/

            if (circle.getCenterX() > event.getX()) {
                circle.setCenterX(event.getX());
            }
            if (circle.getCenterY() > event.getY()) {
                circle.setCenterY(event.getY());
            }
            graphicsContext2D.fillOval(circle.getCenterX(), circle.getCenterY(), circle.getRadius(), circle.getRadius());
            graphicsContext2D.strokeOval(circle.getCenterX(), circle.getCenterY(), circle.getRadius(), circle.getRadius());
            //Тут стек возврата!
        } else if (ellipseBtn.isSelected()) {
            ellipse.setRadiusX(Math.abs(event.getX() - ellipse.getCenterX()));
            ellipse.setRadiusY(Math.abs(event.getY() - ellipse.getCenterY()));
            if (ellipse.getCenterX() > event.getX()) {
                ellipse.setCenterX(event.getX());
            }
            if (ellipse.getCenterY() > event.getY()) {
                ellipse.setCenterY(event.getY());
            }
            graphicsContext2D.strokeOval(ellipse.getCenterX(), ellipse.getCenterY(), ellipse.getRadiusX(), ellipse.getRadiusY());
            graphicsContext2D.fillOval(ellipse.getCenterX(), ellipse.getCenterY(), ellipse.getRadiusX(), ellipse.getRadiusY());

            //Тут стеееек возврата!
        }
    }

    @FXML
    void lineCPAction(ActionEvent event) {
        graphicsContext2D.setStroke(lineCP.getValue());
    }

    @FXML
    void pouringCPAction(ActionEvent event) {
        graphicsContext2D.setStroke(pouringCP.getValue());
    }

    @FXML
    void saveBtnAction(ActionEvent event) {
        FileChooser saveFile = new FileChooser();
        saveFile.setTitle("Save file");

        File file = saveFile.showSaveDialog(stage);
        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, writableImage);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);
            } catch (IOException e) {
                System.out.println("Error!");
            }
        }

    }

}
