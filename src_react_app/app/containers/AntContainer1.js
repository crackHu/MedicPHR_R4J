import React, {
	PropTypes,
} from 'react';
import {
	connect
} from 'react-redux';
import {
	Tabs,
	Button,
	Card,
	Menu,
	Dropdown,
	Icon
} from 'antd';
import QueueAnim from 'rc-queue-anim';
import * as ArchiveActions from '../actions/ArchiveActions'

import {
	msg,
	notify,
	getDate,
	getFieldsObj,
	getFieldsArr
} from 'utils'
import {
	ARC_TYPE_CONFIG,
	PERSONALDETAIL_FIELDS_CONFIG as FIELDS
} from 'phr_conf'

const TabPane = Tabs.TabPane;

/*基本资料 字段*/
const grdaJbzl = FIELDS.grdaJbzl

/*既往史 字段*/
const grdaJws = FIELDS.grdaJws

/*家族史 字段*/
const grdaJzs = FIELDS.grdaJzs

class AntContainer1 extends React.Component {

	arcType = ARC_TYPE_CONFIG.arcType
	specArcType = ARC_TYPE_CONFIG.specArcType
	state = {
		activeKey: this.arcType[0].key,
		arcType: this.arcType,
		[`${FIELDS.name}`]: {},
		submit: false
	}

	componentDidMount = () => {
		console.log('AntContainer1.state', this.state)
	}

	componentWillUnmount = () => {}

	componentWillUpdate = () => {
		console.log('AntContainer1.componentWillUpdate')
	}

	componentDidUpdate = () => {
		console.log("AntContainer1.componentDidUpdate", this.state)
	}

	/*save archiv*/
	saveForm = (e) => {
		this.setState({
			submit: true
		})

		let obj = getFieldsObj(grdaJbzl.fields, this.state[FIELDS.name])
		console.log('getFieldsObj', obj)
		let arr = getFieldsArr(grdaJws.fields, this.state[FIELDS.name])
		console.log('getFieldsArr', arr)

		this.props.saveArchiveData({
			grdaJbzl: obj,
			grdaJws: arr,
			grdaJzs: []
		})
	}

	onFieldsChange = ({
		fields
	}) => {
		console.log('onFieldsChange', fields)
		let state = {}
		state = Object.assign(this.state[FIELDS.name], {}, {
			...fields
		})
		this.setState({
			[`${FIELDS.name}`]: state,
		})
	};

	/*Tab Edit event*/
	onTabEdit = (targetKey, action) => {
		console.log(targetKey, action)
		this[action](targetKey);
	}

	/*Tab remove*/
	remove = (targetKey) => {
		let deleteAbled = true

		/*this.arcType.forEach((pane, i) => {*/
		if ('personalDetail' === targetKey || 'healthMedical' === targetKey) {
			msg('warn', '不能删除档案基本信息表(个人基本信息表、健康体检表)', 3)
			return deleteAbled = false
		}
		/*})*/

		if (deleteAbled) {
			let activeKey = this.state.activeKey;
			let lastIndex;
			this.state.arcType.forEach((pane, i) => {
				if (pane.key === targetKey) {
					lastIndex = i - 1;
				}
			});
			const arcType = this.state.arcType.filter(pane => pane.key !== targetKey);
			if (lastIndex >= 0 && activeKey === targetKey) {
				activeKey = arcType[lastIndex].key;
			}
			this.setState({
				arcType,
				activeKey
			});
		}
	}

	/*Tab switch*/
	changeTab = (activeKey) => {
		this.setState({
			activeKey
		});
	}

	/*add special archiv tab*/
	addSpecArcTab = (tabKey) => {
		const arcType = this.state.arcType;
		let hasNotExist = true
		this.state.arcType.forEach((arcType, index) => {
			if (arcType.key == tabKey) return hasNotExist = false
		})
		if (hasNotExist) {
			this.specArcType.forEach((specArc, index) => {
				if (specArc.key == tabKey) {
					arcType.push({
						name: specArc.name,
						content: specArc.content,
						key: tabKey
					});
					this.setState({
						activeKey: tabKey,
						arcType: arcType
					});
				}
			})
		} else {
			msg('warn', '专档已存在', 3)
		}
	}

	render() {
		const operations = <Button type="primary" onClick={this.saveForm} loading={this.state.submit}>保存档案</Button>
		const moreSpecArc = (
			<Menu>
			    {
			    	this.specArcType.map((arc, index) => {
						return (
						    <Menu.Item key={index}>
						      <a onClick = {() =>this.addSpecArcTab(arc.key)} >{arc.name}</a>
						    </Menu.Item>
						)
					})
				}
		  	</Menu>
		);
		/*trigger={['click']}*/
		const moreSpecArcDd = <Dropdown overlay={moreSpecArc}>
							    <a className="ant-dropdown-link">
							      添加专档 <Icon type="down" />
							    </a>
							  </Dropdown>

		{ /*动态加载档案组件*/ }

		/*{React.createElement(require(`../modules/phr/components/${pane.content}`).default,{
			fields: this.state[FIELDS.name],
			onFieldsChange: this.onFieldsChange
		})}*/
		const tabpane = this.state.arcType.map(pane => {

			let Container = require(`../modules/phr/components/${pane.content}`).default
			return (
				<TabPane tab={pane.name} key={pane.key}>
					<Container
						fields={this.state[FIELDS.name]}
						onFieldsChange={this.onFieldsChange}
					/>
				</TabPane>
			)
		})


		return (
			<QueueAnim delay={10}>
				<div className='module' key="tabs">
					<Card title="新建档案" extra={moreSpecArcDd}>
						<Tabs
							hideAdd
							onChange={this.changeTab}
							activeKey={this.state.activeKey}
							type="editable-card"
							onEdit={this.onTabEdit}
							defaultActiveKey="1"
							tabBarExtraContent={operations}
						>
							{tabpane}
						</Tabs>
					</Card>
				</div>
			</QueueAnim>
		)
	}
}

AntContainer1.propTypes = {
	saveArchiveData: PropTypes.func.isRequired,
	data: PropTypes.object.isRequired
}

function mapStateToProps(state) {
	return {
		data: state
	}
}

export default connect(mapStateToProps, ArchiveActions)(AntContainer1)