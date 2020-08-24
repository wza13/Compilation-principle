import React, { useEffect } from 'react';
import ReactEcharts from 'echarts-for-react';

import echarts from 'echarts/lib/echarts';
import 'echarts/lib/chart/line';
import 'echarts/lib/component/tooltip';
import 'echarts/lib/component/title';

export default function Chart(props) {
  function initial() {
    if (props.chartData.state === 0) {
      return
    }
    var myChart = echarts.init(document.getElementById('treeChart'));
    myChart.setOption({
      title: {
        text: props.chartData.title
      },
      tooltip: {},
      animationDurationUpdate: 1500,
      animationEasingUpdate: 'quinticInOut',
      series: [
        {
          type: 'graph',
          layout: 'none',
          symbolSize: 50,
          roam: true,
          label: {
            show: true,
            formatter: params => params.data.value
          },
          edgeSymbol: ['circle', 'arrow'],
          edgeSymbolSize: [4, 10],
          edgeLabel: {
            fontSize: 20
          },
          data: props.chartData.data,
          // links: [],
          links: props.chartData.links,
          lineStyle: {
            opacity: 0.9,
            width: 2,
            curveness: 0
          }
        }
      ]
    });
  }
  useEffect(() => {
    initial()
  }, [props])
  return (
    <div>
      <div id="treeChart" style={{ width: 1080, height: props.chartData.height }}></div>
    </div>
  );
}

