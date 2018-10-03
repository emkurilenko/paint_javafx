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
import java.util.Optional;
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
    private MenuItem mItemNewFile;

    @FXML
    private MenuItem mItemExit;

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
    private Label labelSliderDottedLine;

    @FXML
    private Slider sliderLine;

    @FXML
    private Slider sliderStrokDash;

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
        /*canvas.setHeight(530);
        canvas.setWidth(650);*/

        lineCP.setValue(Color.BLACK);
        pouringCP.setValue(Color.TRANSPARENT);

        graphicsContext2D = canvas.getGraphicsContext2D();
        graphicsContext2D.setLineWidth(sliderLine.getValue());

        sliderLine.valueProperty().addListener(e -> {
            double width = sliderLine.getValue();
            labelSliderLine.setText(String.format("%.1f", width));
            graphicsContext2D.setLineWidth(width);
        });

        sliderStrokDash.valueProperty().addListener(e -> {
            double w = sliderStrokDash.getValue();
            labelSliderDottedLine.setText(String.format("%.1f", w));
            graphicsContext2D.setLineDashes(w);
        });

        mItemExit.setOnAction(e ->
                //ToDo Dialog save File?
                confirmAlertBeforeLeaving()
        );

        mItemNewFile.setOnAction(e -> {
            graphicsContext2D.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        });


        menuItemRect.setOnAction(e -> rectangleBtn.setSelected(true));

        menuItemCircle.setOnAction(event -> circleBtn.setSelected(true));

        menuItemEllipse.setOnAction(event -> ellipseBtn.setSelected(true));

        mItemColorRed.setOnAction(event -> pouringCP.setValue(Color.RED));

        mItemColorBlue.setOnAction(event -> pouringCP.setValue(Color.BLUE));

        mItemColorGreen.setOnAction(event -> pouringCP.setValue(Color.GREEN));

    }

    private void confirmAlertBeforeLeaving() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.CLOSE);
        alert.setTitle("Сохранить?");
        alert.setHeaderText("Перед уходом не желаете сохранить ваше произведение исскуства?");
        Optional<ButtonType> option = alert.showAndWait();

        if (option.get() == ButtonType.YES) {
            saveBtnAction(new ActionEvent());
            System.exit(0);
        } else if (option.get() == ButtonType.CLOSE) {
            System.exit(0);
        }
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
        graphicsContext2D.setLineDashes(sliderStrokDash.getValue());
        if (drowBtn.isSelected()) {
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
            undoHistory.push(new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY()));
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
            undoHistory.push(new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));
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

            undoHistory.push(new Circle(circle.getCenterX(), circle.getCenterY(), circle.getRadius()));
        } else if (ellipseBtn.isSelected()) {
            ellipse.setRadiusX(Math.abs(event.getX() - ellipse.getCenterX()));
            ellipse.setRadiusY(Math.abs(event.getY() - ellipse.getCenterY()));
            if (ellipse.getCenterX() > event.getX()) {
                ellipse.setCenterX(event.getX());
            }
            if (ellipse.getCenterY() > event.getY()) {
                ellipse.setCenterY(event.getY());
            }
            graphicsContext2D.fillOval(ellipse.getCenterX(), ellipse.getCenterY(), ellipse.getRadiusX(), ellipse.getRadiusY());
            graphicsContext2D.strokeOval(ellipse.getCenterX(), ellipse.getCenterY(), ellipse.getRadiusX(), ellipse.getRadiusY());


            //Тут стеееек возврата!
            undoHistory.push(new Ellipse(ellipse.getCenterX(), ellipse.getCenterY(), ellipse.getRadiusX(), ellipse.getRadiusY()));
        }
        if(!undoHistory.empty()) {
            redoHistory.clear();
            Shape shape = undoHistory.lastElement();
            shape.setStrokeDashOffset(sliderStrokDash.getValue());
            shape.setFill(graphicsContext2D.getFill());
            shape.setStroke(graphicsContext2D.getStroke());
            shape.setStrokeWidth(graphicsContext2D.getLineWidth());
        }
    }

    @FXML
    void lineCPAction(ActionEvent event) {
        graphicsContext2D.setStroke(lineCP.getValue());
    }

    @FXML
    void pouringCPAction(ActionEvent event) {
        graphicsContext2D.setFill(pouringCP.getValue());
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
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    void btnUndoActon(ActionEvent event) {
        if (!undoHistory.empty()) {
            graphicsContext2D.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            Shape removedShape = undoHistory.lastElement();
            if (removedShape.getClass() == Line.class) {
                Line tempLine = (Line) removedShape;
                tempLine.setFill(graphicsContext2D.getFill());
                tempLine.setStroke(graphicsContext2D.getStroke());
                tempLine.setStrokeWidth(graphicsContext2D.getLineWidth());
                tempLine.setStrokeDashOffset(graphicsContext2D.getLineDashes()[0]);
                redoHistory.push(tempLine);
            } else if (removedShape.getClass() == Rectangle.class) {
                Rectangle tempRect = (Rectangle) removedShape;
                tempRect.setFill(graphicsContext2D.getFill());
                tempRect.setStroke(graphicsContext2D.getStroke());
                tempRect.setStrokeWidth(graphicsContext2D.getLineWidth());
                tempRect.setStrokeDashOffset(graphicsContext2D.getLineDashes()[0]);
                redoHistory.push(tempRect);
            } else if (removedShape.getClass() == Circle.class) {
                Circle tempCirc = (Circle) removedShape;
                tempCirc.setStrokeWidth(graphicsContext2D.getLineWidth());
                tempCirc.setFill(graphicsContext2D.getFill());
                tempCirc.setStroke(graphicsContext2D.getStroke());
                tempCirc.setStrokeDashOffset(graphicsContext2D.getLineDashes()[0]);
                redoHistory.push(tempCirc);
            } else if (removedShape.getClass() == Ellipse.class) {
                Ellipse tempElps = (Ellipse) removedShape;
                tempElps.setFill(graphicsContext2D.getFill());
                tempElps.setStroke(graphicsContext2D.getStroke());
                tempElps.setStrokeWidth(graphicsContext2D.getLineWidth());
                tempElps.setStrokeDashOffset(graphicsContext2D.getLineDashes()[0]);
                redoHistory.push(tempElps);
            }

            Shape lastRedo = redoHistory.lastElement();
            lastRedo.setFill(removedShape.getFill());
            lastRedo.setStroke(removedShape.getStroke());
            lastRedo.setStrokeWidth(removedShape.getStrokeWidth());
            lastRedo.setStrokeDashOffset(removedShape.getStrokeDashOffset());
            undoHistory.pop();

            for (int i = 0; i < undoHistory.size(); i++) {
                Shape shape = undoHistory.elementAt(i);
                if (shape.getClass() == Line.class) {
                    Line temp = (Line) shape;
                    graphicsContext2D.setLineWidth(temp.getStrokeWidth());
                    graphicsContext2D.setStroke(temp.getStroke());
                    graphicsContext2D.setFill(temp.getFill());
                    graphicsContext2D.setLineDashes(temp.getStrokeDashOffset());
                    graphicsContext2D.strokeLine(temp.getStartX(), temp.getStartY(), temp.getEndX(), temp.getEndY());
                } else if (shape.getClass() == Rectangle.class) {
                    Rectangle temp = (Rectangle) shape;
                    graphicsContext2D.setLineWidth(temp.getStrokeWidth());
                    graphicsContext2D.setStroke(temp.getStroke());
                    graphicsContext2D.setFill(temp.getFill());
                    graphicsContext2D.setLineDashes(temp.getStrokeDashOffset());
                    graphicsContext2D.fillRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                    graphicsContext2D.strokeRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                } else if (shape.getClass() == Circle.class) {
                    Circle temp = (Circle) shape;
                    graphicsContext2D.setLineWidth(temp.getStrokeWidth());
                    graphicsContext2D.setStroke(temp.getStroke());
                    graphicsContext2D.setFill(temp.getFill());
                    graphicsContext2D.setLineDashes(temp.getStrokeDashOffset());
                    graphicsContext2D.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                    graphicsContext2D.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                } else if (shape.getClass() == Ellipse.class) {
                    Ellipse temp = (Ellipse) shape;
                    graphicsContext2D.setLineWidth(temp.getStrokeWidth());
                    graphicsContext2D.setStroke(temp.getStroke());
                    graphicsContext2D.setFill(temp.getFill());
                    graphicsContext2D.setLineDashes(temp.getStrokeDashOffset());
                    graphicsContext2D.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                    graphicsContext2D.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                }
            }
        }
    }

    @FXML
    void btnRedoActon(ActionEvent event) {
        if(!redoHistory.empty()) {
            Shape shape = redoHistory.lastElement();
            graphicsContext2D.setLineWidth(shape.getStrokeWidth());
            graphicsContext2D.setStroke(shape.getStroke());
            graphicsContext2D.setFill(shape.getFill());
            graphicsContext2D.setLineDashes(shape.getStrokeDashOffset());
            System.out.println(shape.getStrokeDashOffset());

            redoHistory.pop();
            if(shape.getClass() == Line.class) {
                Line tempLine = (Line) shape;
                graphicsContext2D.strokeLine(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY());
                undoHistory.push(new Line(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY()));
            }
            else if(shape.getClass() == Rectangle.class) {
                Rectangle tempRect = (Rectangle) shape;
                graphicsContext2D.fillRect(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight());
                graphicsContext2D.strokeRect(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight());

                undoHistory.push(new Rectangle(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight()));
            }
            else if(shape.getClass() == Circle.class) {
                Circle tempCirc = (Circle) shape;
                graphicsContext2D.fillOval(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius(), tempCirc.getRadius());
                graphicsContext2D.strokeOval(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius(), tempCirc.getRadius());

                undoHistory.push(new Circle(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius()));
            }
            else if(shape.getClass() == Ellipse.class) {
                Ellipse tempElps = (Ellipse) shape;
                graphicsContext2D.fillOval(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY());
                graphicsContext2D.strokeOval(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY());

                undoHistory.push(new Ellipse(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY()));
            }
            Shape lastUndo = undoHistory.lastElement();
            lastUndo.setFill(graphicsContext2D.getFill());
            lastUndo.setStroke(graphicsContext2D.getStroke());
            lastUndo.setStrokeWidth(graphicsContext2D.getLineWidth());
            lastUndo.setStrokeDashOffset(graphicsContext2D.getLineDashes()[0]);
        }
    }

}
