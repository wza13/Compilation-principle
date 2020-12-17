import React, { useEffect, useState } from 'react';
import { makeStyles } from '@material-ui/core/styles';
import ReactDOM from 'react-dom';
import Button from '@material-ui/core/Button';
import Grid from '@material-ui/core/Grid';
import Card from '@material-ui/core/Card';
import CardActionArea from '@material-ui/core/CardActionArea';
import CardActions from '@material-ui/core/CardActions';
import CardContent from '@material-ui/core/CardContent';
import CardMedia from '@material-ui/core/CardMedia';
import Typography from '@material-ui/core/Typography';
import Paper from '@material-ui/core/Paper';
import TextField from '@material-ui/core/TextField';
import Chart from './Chart.js';
import Zoom from '@material-ui/core/Zoom';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';


const useStyles = makeStyles((theme) => ({
  inputContainer: {
    // marginTop: theme.spacing(16),
    display: 'flex',
    flexWrap: 'wrap',
  },
  inputTextArea: {
    width: theme.spacing(64),
  },
  itemS: {
    '& > *': {
      margin: theme.spacing(1),
      height: theme.spacing(7),
    },
  },
  inputCard: {
    marginTop: theme.spacing(8),
    minWidth: 275,
    // backgroundColor: "#000000"
  },
  resultCard: {
    marginTop: theme.spacing(8),
    minWidth: 1080,
    // backgroundColor: "#000000"
  },
  chartContainer: {
    display: 'flex',
    flexWrap: 'wrap',
  }

}));


function App() {
  const classes = useStyles();
  const [chartData, setChartData] = useState(
    {
      state: 0
    }
  )
  const [treeZoom, setTreeZoom] = React.useState(false);
  var count = 0;
  const handleResponse = (text, root) => {
    if (root[text] === "ExpressionError") {
      handleClickOpen()
      return
    }
    root = root[text]
    count = 0;
    var h = getHeight(root)
    var width = 2 ** h
    var treePos = getPos(root, width * 100 * 2, 100, width * 100, (width + 1) * 100 / h)
    var nodes = []
    var edges = []
    setData(null, treePos, nodes, edges)
    console.log({
      state: 1,
      data: nodes,
      links: edges,
      height: ((width + 1) * 100) * 1080 / (width * 100 * 2) + 100,
      title: text
    })
    setChartData(
      {
        state: 1,
        data: nodes,
        links: edges,
        height: ((width + 1) * 100) * 1080 / (width * 100 * 2) + 100,
        title: text
      }
    )

    // 理论H / 理论W = RH / 1080


    setErrorZoom(false)
    setTreeZoom(true)

  }

  function setData(parent, node, nodes, edges) {
    nodes.push(
      {
        name: count++,
        value: node.op.toString(),
        x: node.x,
        y: node.y
      }
    )
    if (parent != null) {
      edges.push(
        {
          source: parent.index,
          target: node.index,
        }
      )
    }

    if (node.left === undefined) {
      return
    }
    setData(node, node.left, nodes, edges)
    setData(node, node.right, nodes, edges)
  }

  function getPos(node, x, y, margin, height) {
    var index = count
    count++
    if (node.op === undefined) {
      return {
        op: node.toString(),
        x: x,
        y: y,
        index: index
      }
    }
    var op = node.op.toString()
    var left = getPos(node.left, x - margin / 2, y + height, margin / 2, height)
    var right = getPos(node.right, x + margin / 2, y + height, margin / 2, height)
    return {
      op: op,
      x: x,
      y: y,
      index: index,
      left: left,
      right: right
    }
  }


  function getHeight(node) {
    if (node.op === undefined) {
      return 1;
    }
    else {
      var left = getHeight(node.left);
      var right = getHeight(node.right);
      return left > right ? left + 1 : right + 1
    }
  }

  const [inputExp, setInputExp] = useState("");
  const handleChange = (event) => {
    setInputExp(event.target.value);
  };

  const [inputInfo, setinputInfo] = useState("算数表达式")

  const [errorZoom, setErrorZoom] = React.useState(false);

  const handleClickOpen = () => {
    setErrorZoom(true);
  };

  const handleClose = () => {
    setErrorZoom(false);
  };
  return (
    <Grid
      container
      direction="column"
      justify="center"
      alignItems="center"

    >
      <Card className={classes.inputCard}>
        <CardContent>
          <div >
            <Paper elevation={0} className={classes.inputContainer} >
              <Grid
                container
                direction="row"
                justify="center"
                alignItems="center"
              >
                <div className={classes.itemS}>
                  <TextField
                    className={classes.inputTextArea}
                    // id="mathInput"
                    label={inputInfo}
                    color="secondary"
                    id="outlined-name"
                    // label="Name"
                    // helperText="Error"
                    value={inputExp}
                    onChange={handleChange}
                    variant="outlined"
                  />
                  <Button variant="contained" color="primary" size="large" onClick={() => {
                    // setTreeZoom(false)
                    fetch('./postApi', {
                      method: 'POST',
                      credentials: "include",
                      headers: {
                        'Content-Type': 'application/json;charset=UTF-8'
                      },
                      body: inputExp,
                      mode: 'cors',
                      cache: 'no-store'
                    }).then(response => response.json())
                      .then(data => handleResponse(inputExp, data))
                      .catch(err => {
                        handleClickOpen()
                        console.log(err)
                      })
                  }}>
                    分析
                  </Button>
                  <Button variant="contained" color="secondary" size="large" onClick={() => {
                    setInputExp("")
                    setTreeZoom(false)
                    setChartData({
                      state: 0
                    })
                  }}>
                    清空
                  </Button>
                </div>
              </Grid>
            </Paper>
          </div>
        </CardContent>
      </Card>
      <Zoom in={treeZoom}>
        <Card className={classes.resultCard}>
          <CardContent>
            <Paper elevation={0} className={classes.chartContainer} >
              <Chart chartData={chartData} />
            </Paper>
          </CardContent>
        </Card>
      </Zoom>
      <Dialog
        open={errorZoom}
        onClose={handleClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">{"Error"}</DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            请检查输入是否有误
          </DialogContentText>
        </DialogContent>
      </Dialog>

    </Grid>
  );
}

export default App;
